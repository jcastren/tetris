package fi.valagroup.joonas;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class TetrisApp {
    public static void main( String[] args ) {
        System.out.println("Hello World!");
        
        String fileName;
        
        if (args == null) {
        	System.out.println("Give file name as argument");
        } else {
        	fileName = args[0];
        	System.out.println("fileName read: " + fileName);
        	
        	List<String> lines = new ArrayList<String>();
        	try {
				 lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
			} catch (IOException e) {
				System.out.println(String.format("Reading the file with fileName <%s> failed, exception: %s", fileName, e.toString()));
				e.printStackTrace();
			}
        	
        	/*
		 	A:0,0;1,0;1,1;2,1
			B:0,0;0,1;0,2;1,2
			C:0,0;1,0;2,0;1,1
			D:0,0;1,0;1,1;1,-1
        	*/
        	int lineNumber = 1;
        	for (String line : lines) {
        		System.out.println(String.format("line %s contains string: %s", Integer.toString(lineNumber), line));
        		
        		TetrisBlock block = new TetrisBlock();
        		
        		String blockId = line.substring(0, 1);
        		
        		
        		
        		//while (remaining.length() > 4){
        			
        			Pattern pattern = Pattern.compile("((-)*\\d+),((-)*\\d+)");
        			Matcher matcher = pattern.matcher(line);
        			
        			while (matcher.find()) {
        				//out(String.format("matcher: %s, groupCount: %s ", matcher.toString(), Integer.valueOf(matcher.groupCount()).toString()));
        				
        				/*
        				for (int i = 0; i <= matcher.groupCount(); i++) {
        					out(String.format("matcher.group[%s] = %s", Integer.valueOf(i).toString(), matcher.group(i)));
        				}
        				*/
        				
        				//out("X coordinate found: " + matcher.group(1));
        				//out("Y coordinate found: " + matcher.group(3));
        				
        				int x = Integer.parseInt(matcher.group(1));
            			int y = Integer.parseInt(matcher.group(3));
            			
            			//remaining = remaining.substring(4);
            			
            			Coordinate coord = new Coordinate(x, y, blockId);
            			
            			System.out.println(coord.toString());
            			
            			block.addCoordinate(new Coordinate(x, y, blockId));
        			}
        			
        			/*
        			int x = Integer.parseInt(remaining.substring(0, 1));
        			int y = Integer.parseInt(remaining.substring(2, 3));
        			
        			remaining = remaining.substring(4);
        			
        			Coordinate coord = new Coordinate(x, y, blockId);
        			
        			System.out.println(coord.toString());
        			
        			block.addCoordinate(new Coordinate(x, y, blockId));
        			*/
        			
        		//}
        		
        		
        	}
        }
    }
    
    private static void out(String str) {
    	System.out.println(str);
    }
    
}
