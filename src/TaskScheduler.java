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
/*
 * this class is to describe a task_tuple.
 * For the task_queue, the rd_time is deadline. For the schedule_queue, the rd_time is 
 * release time. So that we can insert Entry<release time, Task_Tuple(deadline, task_name)> 
 * into task_queue. Meanwhile, insert Entry<deadline time, Task_Tuple(release time, task_name)> 
 * into schedule queue for scheduling.
 */
class Task_Tuple{
	 Integer rd_time;   //this rd_time could either be the release time or deadline time. 
	 String task_name;
	 Task_Tuple(){}
	 Task_Tuple(Integer time, String name){rd_time = time; task_name = name;}
	 void Set_time(Integer t){rd_time =t;}
	 void Set_name(String name){task_name = name;}
}

public class TaskScheduler{
	//we use regex patterns to check the validity of the input of tasks.
	static private String task_name_pattern = "([a-z])([0-9])*";
	static private String r_time_pattern = "(\\d)(\\d)*";
	static private String d_time_pattern = "([1-9])(\\d)*";
	//task_queue is a priority queue of tasks, the keys of which indicate release time.
	static HeapPriorityQueue<Integer,Task_Tuple> task_queue = new HeapPriorityQueue<Integer,Task_Tuple>();
	//schedule_queue is a priority queue for the scheduler to implement EDF scheduling. The keys of this queue is the deadline time.
	static HeapPriorityQueue<Integer,Task_Tuple> schedule_queue = new HeapPriorityQueue<Integer,Task_Tuple>();

	/*
	 * The time complexity of scheduler is O(nlogn), where n is the number of tasks. 
	 * Firstly, we insert the tasks in the input file to task_queue. For each task, the time consumed 
	 * to insert it into the task_queue is O(logn). So the time complexity of this step is O(nlogn). 
	 * Secondly, we remove the tasks from task_queue according to time and insert these tasks into the 
	 * schedule_queue. Each remove and insert cost O(logn) time due to the down/up-heap. So the insertions
	 * and removes cost O(nlogn) time, repectively. 
	 * At the same time, we remove the tasks in schedule_queue according to the deadline and assign them to 
	 * different CPU cores. All the removes cost O(nlogn) time. 
	 * Overall, the time complexity is O(nlogn).
	 */
	static void scheduler(String file1, String file2, int m){
		Scanner file1_scanner;
		//we read input file file1.txt by a scanner, and FileNotFound exception is handled. 
		try{
			file1_scanner = new Scanner(new FileReader(file1));
		}catch(FileNotFoundException e){
			System.out.println("File1 does not exsist.");
			return;
		}
		
		while(file1_scanner.hasNext())   //firstly, add all the tasks into a priority task queue. The key of this queue is the release time.
		{
			Task_Tuple tmp = new Task_Tuple();
			Integer release_time = null;  //the temporary release time variable for 
			String nextline; //a temporary variable for the next line of the scanner. 
			String cur_task_name = ""; //this variable is used to record the task name 
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

						
				}catch(NoSuchElementException er)
				{
						System.out.println("input error while reading the attributes of task "+ cur_task_name);
						er.printStackTrace();
						return;
				}
				
			}
			task_queue.insert(release_time.intValue(), tmp);//insert a task into the task queue and use the release time as the key.
		}
		Entry<Integer, Task_Tuple> task_tmp = task_queue.removeMin();
		
		//secondly, we schedule the tasks with time sequence. 
		schedule_queue.insert(task_tmp.getValue().rd_time, new Task_Tuple(task_tmp.getKey(), task_tmp.getValue().task_name));
		//this writer is used to write the output file file2.txt
		BufferedWriter out_file =  null;
		try{
			out_file = new BufferedWriter(new FileWriter(file2));}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		String write_buffer_string = new String("");
		//cur_time is the current time which is a trigger signal for scheduling.
		int cur_time = 0;
		while((!task_queue.isEmpty())||(!schedule_queue.isEmpty())){
			//this loop is used to add released tasks into the schedule_queue for further scheduling.
			while((!task_queue.isEmpty())&&(task_queue.min().getKey()==cur_time))
			{	
				Entry<Integer, Task_Tuple> cur_task = task_queue.removeMin();

				schedule_queue.insert(cur_task.getValue().rd_time, new Task_Tuple(cur_task.getKey(), cur_task.getValue().task_name));
			}
			//this loop is to schedule 2 tasks to two cores. 
			for(int j = 0 ; j < m; j++)
			{
				if(!schedule_queue.isEmpty())
				{
					Entry<Integer, Task_Tuple> tmp_sche_result = schedule_queue.removeMin();
					/*if there is a task added before which has not been scheduled and the deadline of that
					 * task is passed. Then it indicates that a feasible scheduling is not possible. We write 
					 * this info to the output file and return. 
					 */
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
						/*
						 * if the current task can still be scheduled. Then we assign a core to the task. Then 
						 * we remove this task from schedule_queue. The info of this scheduling is written into 
						 * the write buffer which would be written into output file later. 
						 */
						System.out.printf("task for time %d is %s\n", cur_time, tmp_sche_result.getValue().task_name);
						write_buffer_string = write_buffer_string + tmp_sche_result.getValue().task_name + " " + cur_time + " ";
						
					}
				}
			}
			cur_time++;
		}
		//write the buffer to the output file. 
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