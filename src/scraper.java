//Jack Jamieson
//http://jsoup.org/
//Add a timer
//add a counter
//import java.io.BufferedReader;

import java.io.BufferedWriter;
//import java.io.File;
import java.io.FileInputStream;
//import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import org.jsoup.*;
import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.net.ftp.FTPClient;



public class scraper extends TimerTask{
	
String FPposts[] = new String[26];//25 posts on the current front page.
Object[] FPwordsArr;
LinkedList<Object> FPwords = new LinkedList<Object>();
//String[] rejectedWords = {"a", "an", "that", "this", "for", "to", "the", "as", "", "with", "in", "from", "and", "of", "on", "is", "i", "my", "it", "so", "you", "me", "what", "was" , "at"};
//ArrayList
Timer timer;

	

	public void execute() throws IOException
	{
		scrape();
		outputFront();
		outputWords();
		upload();
		schedule();
	}

	@SuppressWarnings("resource")
	public void scrape() throws IOException{

		Document site = Jsoup.connect("http://www.reddit.com/.rss").get();//Using Jsoup to connect to and parse Reddit.
		Elements titles = site.select("title");//Create a list of elements that use the title tag.
		
		//Removing junk from the string.
		String posts = titles.toString();

		posts = posts.toLowerCase();//Normalize in lower case.

		posts = posts.substring(107);//Removes the Reddit homepage titles.
		posts = posts.replaceAll("<title>", "");
		posts = posts.replaceAll("</title>", "");
		posts = posts.replaceAll("&quot;", "\"");

		//Split the string by post.
		Scanner SplitPosts = new Scanner(posts);
		SplitPosts.useDelimiter("\n");
		Scanner ReadWords = new Scanner(posts);
		
		int postnum = 0;
		while(SplitPosts.hasNextLine() && postnum < 26)
		{		
			String line = SplitPosts.nextLine();
			FPposts[postnum] = line;
			//System.out.println(FPposts[postnum] + " - " + postnum);
			postnum++;
		}
		
		FPposts[0] = "";
		//SplitPosts.reset();
		
		while(ReadWords.hasNext())//Making a linked list of words.
		{		
			String line = ReadWords.next();
			line = line.replace(".", "");//Remove periods from ends of sentences, etc.
			line = line.replace(",", "");//Remove periods from ends of sentences, etc.
//			line = line.replace("the", "");
//			line = line.("a", "");
//			line = line.replace("an", "");
//			line = line.replace("like", "");
//			line = line.replace("for", "");
//			line = line.replace("in", "");
//			line = line.replace("on", "");
//			line = line.replace("is", "");
//			line = line.replace("that", "");
//			line = line.replace("to", "");
			FPwords.add(line);
			
			
			//postnum++;
		}
		
		//System.out.println(FPwords);
		FPwordsArr = FPwords.toArray();
		Arrays.sort(FPwordsArr);
		//System.out.println(FPwordsArr[0].toString());
		//System.out.println(FPposts[24]);
		//System.out.println(FPwordsArr[0]);

	}
	
	public void outputFront() throws IOException
	{
		FileWriter fw = new FileWriter("FrontPageTitles.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(int start = 1; start < 26; start++)
		{
			bw.write(FPposts[start] + '\n');	
		}
		
		bw.flush();
		bw.close();
	}
	
	public void outputWords() throws IOException
	{
		//Object[] FPwordsArrFULL;
		//Object result[] = new Object[FPwordsArr.length];
		int count = 0;
		String temp = "";
		FileWriter fw = new FileWriter("ALLFrontPagedWords.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		
		//FileInputStream fstream = new FileInputStream("ALLFrontPagedWords.txt");
		//BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		
		for(int start = 0; start < FPwordsArr.length; start++)
		{
			//String reading = br.readLine();
			if(FPwordsArr[start].toString().equals("to") || FPwordsArr[start].toString().equals("a") || FPwordsArr[start].equals("the") || FPwordsArr[start].equals("a") || FPwordsArr[start].equals("") || FPwordsArr[start].equals("with") || FPwordsArr[start].equals("in") || FPwordsArr[start].equals("from") || 
					FPwordsArr[start].equals("this") || FPwordsArr[start].equals("that") || FPwordsArr[start].equals("and") || FPwordsArr[start].equals("of") || FPwordsArr[start].equals("on") || FPwordsArr[start].equals("is") || FPwordsArr[start].equals("i") || FPwordsArr[start].equals("my") || FPwordsArr[start].equals("it")
					|| FPwordsArr[start].equals("as") || FPwordsArr[start].equals("an") || FPwordsArr[start].equals("so") || FPwordsArr[start].equals("you") || FPwordsArr[start].equals("my") || FPwordsArr[start].equals("me") || FPwordsArr[start].equals("what") || FPwordsArr[start].equals("was") || FPwordsArr[start].equals("at")
					|| FPwordsArr[start].equals("for"))
			{
				//Ignore these words.
			}
			else
			{
				for(int rest = start;rest < FPwordsArr.length; rest++)
					if(FPwordsArr[start].toString().equals(FPwordsArr[rest].toString()))
					{
						count++;

						if(temp.equals(FPwordsArr[rest]))
							FPwordsArr[rest] = "";	
						
						temp = FPwordsArr[start].toString();
						//FPwordsArrFULL = FPwordsArr.clone();
						//FPwordsArr[rest] = "";
					}
				if(FPwordsArr[start].equals(""))
				{
					//Ignore empty words and words that were seen already.
				}
				else
				{
					FPwordsArr[start] = "Seen " + count + " " + FPwordsArr[start].toString();
					
					//bw.append(FPwordsArr[start].toString() + " - seen " + count + " times");	
	
				}
				
				count = 0;
			}
	
		}	
		Arrays.sort(FPwordsArr, Collections.reverseOrder());
		for(int write = 0; write < FPwordsArr.length; write++)
		{
			if(FPwordsArr[write].toString().equals("to") || FPwordsArr[write].toString().equals("a") || FPwordsArr[write].equals("the") || FPwordsArr[write].equals("a") || FPwordsArr[write].equals("") || FPwordsArr[write].equals("with") || FPwordsArr[write].equals("in") || FPwordsArr[write].equals("from") || 
					FPwordsArr[write].equals("this") || FPwordsArr[write].equals("that") || FPwordsArr[write].equals("and") || FPwordsArr[write].equals("of") || FPwordsArr[write].equals("on") || FPwordsArr[write].equals("is") || FPwordsArr[write].equals("i") || FPwordsArr[write].equals("my") || FPwordsArr[write].equals("it")
					|| FPwordsArr[write].equals("as") || FPwordsArr[write].equals("an") || FPwordsArr[write].equals("so") || FPwordsArr[write].equals("you") || FPwordsArr[write].equals("my") || FPwordsArr[write].equals("me") || FPwordsArr[write].equals("what") || FPwordsArr[write].equals("was") || FPwordsArr[write].equals("at")
					|| FPwordsArr[write].equals("for"))
					{
					
					}
					else{
						bw.append(FPwordsArr[write].toString());
						bw.newLine();
					}
				
			}

		
		bw.flush();
		bw.close();
	}
	
	@Override
	public void run()
	{
		try {
			execute();
			timer.cancel();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERROR: Failed to execute the timer.");
		}
	}
	
	public void schedule()
	{
		timer = new Timer();
		TimerTask t = new scraper();
		timer.scheduleAtFixedRate(t, 0, 300000);
	}
	
	public void upload()
	{
		String server = "your server name";
		String user = "user name";
		String pw = "password";
		
		FTPClient ftp = new FTPClient();
		FileInputStream fis = null;
		FileInputStream fis2 = null;
		try{
			ftp.connect(server);
			ftp.login(user, pw);
			fis = new FileInputStream("ALLFrontPagedWords.txt");
			fis2 = new FileInputStream("FrontPageTitles.txt");
			ftp.changeWorkingDirectory("working directory");
			ftp.storeFile("ALLFrontPagedWords.txt", fis);
			ftp.storeFile("FrontPageTitles.txt", fis2);
			ftp.logout();		
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("ERROR: FTP error.");
			
		}
		finally{
			try{
				if(fis != null){
					fis.close();
				}
				ftp.disconnect();
			}catch (IOException e){
				e.printStackTrace();
			}
				
			
		}
	}
}
