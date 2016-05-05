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

class Task_Tuple{
	 Integer rd_time;   //this rd_time could either be the release time or deadline time. 
	 String task_name;
	 Task_Tuple(){}
	 Task_Tuple(Integer time, String name){rd_time = time; task_name = name;}
	 void Set_time(Integer t){rd_time =t;}
	 void Set_name(String name){task_name = name;}
}

public class TaskScheduler{
	
	static private String task_name_pattern = "([a-z])([0-9])*";
	static private String r_time_pattern = "(\\d)(\\d)*";
	static private String d_time_pattern = "([1-9])(\\d)*";
//	static private String file1_pattern = "(([a-z])([a-z]\\d)*(\\s)*)((\\d)(\\d)*(\\s)*)(([1-9])(\\d)*(\\s)*)";

//	static Pattern file1_p = Pattern.compile(file1_pattern);
	
	static HeapPriorityQueue<Integer,Task_Tuple> task_queue = new HeapPriorityQueue<Integer,Task_Tuple>();
	
	static HeapPriorityQueue<Integer,Task_Tuple> schedule_queue = new HeapPriorityQueue<Integer,Task_Tuple>();
	//static String write_buffer_string = new String("");
	//m is the number of cpu cores. 
	static void scheduler(String file1, String file2, int m){
		Scanner file1_scanner;
		try{
			file1_scanner = new Scanner(new FileReader(file1));
			
		}catch(FileNotFoundException e){
			System.out.println("File1 does not exsist.");
			return;
		}
		StringBuffer task_buffer = new StringBuffer();
		while(file1_scanner.hasNext())   //firstly, add all the tasks into a priority task queue. The key of this queue is the release time.
		{
			Task_Tuple tmp = new Task_Tuple();
			Integer release_time = -1;
			String nextline;
			String cur_task_name = "";
			for(int i = 0; i < 3; i++)
			{
				try{
						if(i == 0) //as we use regex pattern, the case of task_name should be processed carefully. 
						{
							try{
								nextline = file1_scanner.next(task_name_pattern);
								cur_task_name = nextline; //if unmatch is not existed in task_name, we just record the right name.
								tmp.Set_name(nextline);
							}catch(InputMismatchException e)
							{
								cur_task_name = file1_scanner.next(); //if unmatch happens in task_name, we should re-read to get the wrong task name.  
								throw e;
							}
							
						}
						else if(i == 2)
						{
							nextline = file1_scanner.next(d_time_pattern);
							tmp.Set_time(Integer.parseInt(nextline));
						}
						else
						{
							nextline = file1_scanner.next(r_time_pattern);
							release_time = Integer.parseInt(nextline);
						}
						task_buffer.append(nextline);
						task_buffer.append(" ");
						
				}catch(NoSuchElementException er)
				{
						System.out.println("input error while reading the attributes of task "+ cur_task_name);
						er.printStackTrace();
						return;
				}
				
			}
			task_queue.insert(release_time.intValue(), tmp);
		}
		Entry<Integer, Task_Tuple> task_tmp = task_queue.removeMin();
		//secondly, we schedule the tasks with time sequence. 
		schedule_queue.insert(task_tmp.getValue().rd_time, new Task_Tuple(task_tmp.getKey(), task_tmp.getValue().task_name));



		//StringBuffer write_buffer = new StringBuffer("");
		
		BufferedWriter out_file =  null;
		try{
			out_file = new BufferedWriter(new FileWriter(file2));}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		String write_buffer_string = new String("");
		int cur_time = 0;
		while((!task_queue.isEmpty())||(!schedule_queue.isEmpty())){
			while((!task_queue.isEmpty())&&(task_queue.min().getKey()==cur_time))
			{	
				Entry<Integer, Task_Tuple> cur_task = task_queue.removeMin();

				schedule_queue.insert(cur_task.getValue().rd_time, new Task_Tuple(cur_task.getKey(), cur_task.getValue().task_name));
			}
			for(int j = 0 ; j < m; j++)
			{
				if(!schedule_queue.isEmpty())
				{
					Entry<Integer, Task_Tuple> tmp_sche_result = schedule_queue.removeMin();
					if(tmp_sche_result.getKey() <= cur_time)
					{
						System.out.println("No feasible schedule exists");
						write_buffer_string = "No feasible schedule exists";
						try{
							out_file.write(write_buffer_string);

							out_file.close();
						}catch(IOException e)
						{
							System.out.println("IO exception");
							e.printStackTrace();
						}

						return;
					}
					else{
						//write_buffer.append(tmp_sche_result.getValue().task_name).append(" ").append(cur_time).append(" ");
						System.out.printf("task for time %d is %s\n", cur_time, tmp_sche_result.getValue().task_name);
						write_buffer_string = write_buffer_string + tmp_sche_result.getValue().task_name + " " + cur_time + " ";
						
					}
				}
			}
			cur_time++;
		}
		try{
			out_file.write(write_buffer_string);
			out_file.close();
		}catch(IOException e)
		{
			System.out.println("IO exception");
			e.printStackTrace();
		}
		System.out.println(write_buffer_string);
	}
	
	public static void main(String[] args)
	{
		
		TaskScheduler.scheduler("/home/wuhuijun/java_workspace/comp9024_assignment3/src/file1.txt", "/home/wuhuijun/java_workspace/comp9024_assignment3/src/file2.txt", 2);
		
	}
}