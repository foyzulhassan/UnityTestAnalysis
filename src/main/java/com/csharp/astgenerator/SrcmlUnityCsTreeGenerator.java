package com.csharp.astgenerator;



import com.github.gumtreediff.gen.Register;
import com.github.gumtreediff.gen.antlr3.r.RParser.sequence_return;
import com.github.gumtreediff.gen.srcml.AbstractSrcmlTreeGenerator;
import com.github.gumtreediff.tree.TreeContext;
import com.github.gumtreediff.tree.Type;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import com.github.gumtreediff.io.LineReader;
import com.github.gumtreediff.tree.ITree;

import com.github.gumtreediff.tree.TreeContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.*;

import javax.xml.stream.XMLInputFactory;
import static com.github.gumtreediff.tree.TypeSet.type;

@Register(id = "cs-srcml", accept = "\\.cs$")
public class SrcmlUnityCsTreeGenerator extends AbstractSrcmlTreeGenerator {
	
	 protected static final QName LINE_START = new  QName("http://www.srcML.org/srcML/position", "start","pos");
	 protected static final QName LINE_END = new  QName("http://www.srcML.org/srcML/position", "end","pos");
	 private static int nodeid = 0;
	 
    @Override
    public String getLanguage() {
        return "C#";
    } 


    @Override
    public TreeContext generate(Reader r) throws IOException {
        lr = new LineReader(r);
        String output = readStandardOutput(lr);
        return getTreeContext(output);
    }

    public TreeContext getTreeContext(String xml) {
        XMLInputFactory fact = XMLInputFactory.newInstance();
        context = new TreeContext();
        currentLabel = new StringBuilder();
        try {
            ArrayDeque<ITree> trees = new ArrayDeque<>();
            XMLEventReader r = fact.createXMLEventReader(new StringReader(xml));
            while (r.hasNext()) {
                XMLEvent ev = r.nextEvent();
                if (ev.isStartElement()) {
                    StartElement s = ev.asStartElement();
                    Location abc=s.getLocation();
                  
                    Type type = type(s.getName().getLocalPart());

                    if (type.equals(position))
                        setLength(trees.peekFirst(), s);
                    else {
                        ITree t = context.createTree(type, "");
                        t.setMetadata("id",nodeid);
                        nodeid++;
                        if (trees.isEmpty()) {
                            context.setRoot(t);
                            t.setPos(0);
                        } else {
                            t.setParentAndUpdateChildren(trees.peekFirst());
                            setPos(t, s);
                        }
                        trees.addFirst(t);
                    }
                } else if (ev.isEndElement()) {
                    EndElement end = ev.asEndElement();
                    if (type(end.getName().getLocalPart()) != position) {
                        if (isLabeled(trees))
                            trees.peekFirst().setLabel(currentLabel.toString());
                        trees.removeFirst();
                        currentLabel = new StringBuilder();
                    }
                } else if (ev.isCharacters()) {
                    Characters chars = ev.asCharacters();
                    if (!chars.isWhiteSpace() && isLabeled(trees))
                        currentLabel.append(chars.getData().trim());
                }
            }
            fixPos(context);
            return context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isLabeled(ArrayDeque<ITree> trees) {
        return labeled.contains(trees.peekFirst().getType());
    }

    private void fixPos(TreeContext ctx) {
        for (ITree t : ctx.getRoot().postOrder()) {
            if (!t.isLeaf()) {
                if (t.getPos() == ITree.NO_POS || t.getLength() == ITree.NO_POS) {
                    ITree firstChild = t.getChild(0);
                    t.setPos(firstChild.getPos());
                    if (t.getChildren().size() == 1)
                        t.setLength(firstChild.getLength());
                    else {
                        ITree lastChild = t.getChild(t.getChildren().size() - 1);
                        t.setLength(lastChild.getEndPos() - firstChild.getPos());
                    }
                }
            }
        }
    }

//    private void setPos(ITree t, StartElement e) {
//    	
//    	Attribute atr=e.getAttributeByName(LINE_START);
//    	if(atr!=null)
//    	{
//    		String atrval=atr.getValue();
//    		System.out.println(atrval);
//    	}
//    	
//        if (e.getAttributeByName(LINE) != null) {
//            int line = Integer.parseInt(e.getAttributeByName(LINE).getValue());
//            int column = Integer.parseInt(e.getAttributeByName(COLUMN).getValue());
//            t.setPos(lr.positionFor(line, column));
//        }
//    }
    
	private void setPos(ITree t, StartElement e) {

		//Attribute atr = e.getAttributeByName(LINE_START);
		int line=0;
		int column=0;
		int pos=0;

		if (e.getAttributeByName(LINE_START) != null) {

			line = getFirstPart(e.getAttributeByName(LINE_START));
			column = getSecondPart(e.getAttributeByName(LINE_START));
			
			pos=lr.positionFor(line, column);
			t.setPos(pos);
			t.setMetadata("lineno", line);
			//t.setPos(line);	
			//t.setLength(column);
		}		

      if (e.getAttributeByName(LINE_END) != null) {

    	  	int lastline = getFirstPart(e.getAttributeByName(LINE_END));
			int lastcolumn = getSecondPart(e.getAttributeByName(LINE_END));	
			int end=lastline+lastcolumn;
			int length=lr.positionFor(lastline, lastcolumn) - t.getPos() + 1;
			t.setLength(length);		
			//int length=end-pos+1;
			//t.setLength(length);			
		}
	}

    private void setLength(ITree t, StartElement e) {
        if (t.getPos() == -1)
            return;
//        if (e.getAttributeByName(LINE) != null) {
//            int line = Integer.parseInt(e.getAttributeByName(LINE).getValue());
//            int column = Integer.parseInt(e.getAttributeByName(COLUMN).getValue());
//            t.setLength(lr.positionFor(line, column) - t.getPos() + 1);
//        }
        
        if (e.getAttributeByName(LINE_END) != null) {

    	  	int lastline = getFirstPart(e.getAttributeByName(LINE_END));
			int lastcolumn = getSecondPart(e.getAttributeByName(LINE_END));	
			int end=lastline+lastcolumn;
		    t.setLength(lr.positionFor(lastline, lastcolumn) - t.getPos() + 1);				
		}
    }


    public String[] getCommandLine(String file) {
        return new String[]{SRCML_CMD, "-l", getLanguage(), "--position", file, "--tabs=1"};
    }
    
    public int getFirstPart(Attribute atr)
    {
    	int first=0;
    	
    	String atrval=atr.getValue();
    	
    	String arr[] = atrval.split(":");
    	
    	first=Integer.parseInt(arr[0]);
    	
    	return first;
    	
    }
    
    public int getSecondPart(Attribute atr)
    {
    	int second=0;
    	
    	String atrval=atr.getValue();
    	
    	String arr[] = atrval.split(":");
    	second=Integer.parseInt(arr[1]);
    	
    	return second;
    	
    }
}
