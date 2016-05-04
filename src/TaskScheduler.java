/*
 *Comp9024_Assignment3
 *Author: Huijun Wu(z5055605)
 *Date:04/05/2016 
 */

import net.datastructures.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;
import java.lang.*;

public class TaskScheduler{
	
	static private String task_name_pattern = "([a-z])([a-z]\\d)*";
	static private String r_time_pattern = "(\\d)(\\d)*";
	static private String d_time_pattern = "([1-9])(\\d)*";
	static private String file1_pattern = "(([a-z])([a-z]\\d)*(\\s)*)((\\d)(\\d)*(\\s)*)(([1-9])(\\d)*(\\s)*)";
	
	static Pattern t_name_p = Pattern.compile(task_name_pattern);
	static Pattern r_time_p = Pattern.compile(r_time_pattern);
	static Pattern d_time_p = Pattern.compile(d_time_pattern);
	static Pattern file1_p = Pattern.compile(file1_pattern);
			
	
	static void scheduler(String file1, String file2){
		Scanner file1_scanner;
		try{
			file1_scanner = new Scanner(new FileReader(file1));
			
		}catch(FileNotFoundException e){
			System.out.println("File1 does not exsit.");
			return;
		}
		StringBuffer task_buffer = new StringBuffer();
		while(file1_scanner.hasNextLine())
		{
			for(int i = 0; i < 3; i++)
			{
				try{
						task_buffer.append(file1_scanner.nextLine());
				}catch(NoSuchElementException er)
					{
							System.out.println("File1 is not complete");
							return;
					}
				
			}
			Matcher m = file1_p.matcher(task_buffer);
			if(m.find()){
				System.out.println("Found " + m.group());
			}
			else
			{
				System.out.println("File1 format error!");
				return;
			}
			
		}

	}
	
	public static void main(String[] args)
	{
		String a = "a 0 1 b 2 4 c 2 3 ";
		Matcher m = file1_p.matcher(a);
		scheduler("a","b");

		
		//{
		//	System.out.println("No matches");
		//}
		
		return;
		
		
	}
}