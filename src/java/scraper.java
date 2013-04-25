//Written by Jack Jamieson
//Reddit Front Page Word Analyzer
//Does all of the backend work.
//Dependencies: jsoup, apache commons net, apache commons io.
//v0.2.1
//https://twitter.com/jamieson_jack

//    This file is part of RedditFPWAnalyzer.
//
//    RedditFPWAnalyzer is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    RedditFPWAnalyzer is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with RedditFPWAnalyzer.  If not, see <http://www.gnu.org/licenses/>.

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
//import java.util.Arrays;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import java.util.Date;

public class scraper
	
String FPposts[] = new String[26];//25 posts on the current front page.  There is an extra array location because [0] contains nothing.
Object[] FPwordsArr;//The array created from FPwords.
//LinkedList<Object> FPwordsArrAllTime = new LinkedList<Object>();//Keep track of all time most popular words.
Object[] FPmatched;//The array created from matchingPosts.
LinkedList<Object> FPwords = new LinkedList<Object>();//Contains all words to hit the front page.
LinkedList<Object> matchingPosts = new LinkedList<Object>();//Contains all CURRENT matching posts.

Object[] FPwordsArrAllTime;
//STOP WORDS! Thanks to www.textfixer.com
String stopWords[] = {"'tis","'twas","a","able","about","across","after","ain't","all","almost","also","am","among","an","and","any","are","aren't","as","at","be","because","been","but","by","can","can't","cannot","could","could've","couldn't","dear","did","didn't","do","does","doesn't","don't","either","else","ever","every","for","from","get","got","had","has","hasn't","have","he","he'd","he'll","he's","her","hers","him","his","how","how'd","how'll","how's","however","i","i'd","i'll","i'm","i've","if","in","into","is","isn't","it","it's","its","just","least","let","like","likely","may","me","might","might've","mightn't","most","must","must've","mustn't","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","shan't","she","she'd","she'll","she's","should","should've","shouldn't","since","so","some","than","that","that'll","that's","the","their","them","then","there","there's","these","they","they'd","they'll","they're","they've","this","tis","to","too","twas","us","wants","was","wasn't","we","we'd","we'll","we're","were","weren't","what","what'd","what's","when","when","when'd","when'll","when's","where","where'd","where'll","where's","which","while","who","who'd","who'll","who's","whom","why","why'd","why'll","why's","will","with","won't","would","would've","wouldn't","yet","you","you'd","you'll","you're","you've","your", "", "up", "very", "-", "each", "came", "even", "new", "=", "do's", "don'ts", "its", "isnt", "now"};

	public void execute() throws IOException, InterruptedException
	{
		//long startTime = System.currentTimeMillis();
		//long elapsed = 0;
		int run = 0;//Run infinite.
		while(run == 0)
		{
			scrape();
			outputFront();
			outputWords();
			matchPosts();
			upload("your server", "username", "password");
			Thread.sleep(120000);//Amount of time between scrapes - currently running every 2 minutes.
			//elapsed = (new Date()).getTime() - startTime;
		}
		//schedule(0, 120000);//Example time limits.  This will run every 7 minutes. 2 minute delay, 5 minute interval.
	}

	//Performs the initial parse and normalizes the data.
	@SuppressWarnings("resource")
	public void scrape() throws IOException{

		//Using the RSS feed was easier because there is less formatting to sort through.
		Document site = Jsoup.connect("http://www.reddit.com/.rss")
				.userAgent("Reddit Front Page Word Analyzer/Trending FP Words on Reddit by worldbit v0.2.1")
				.get();//Using Jsoup to connect to and parse Reddit's RSS feed.
	
		Elements titles = site.select("title");//Create a list of elements that use the title tag.  Title defines the names of the posts.
		
		//Removing junk from the string.
		String posts = titles.toString();
		posts = posts.toLowerCase();//Normalize in lower case.
		posts = posts.substring(107);//Removes the Reddit homepage titles.
		posts = posts.replaceAll("<title>", "");
		posts = posts.replaceAll("</title>", "");
		posts = posts.replaceAll("&quot;", "\"");

		//Split the string by post.
		Scanner SplitPosts = new Scanner(posts);
		SplitPosts.useDelimiter("\n");//Every new line is a new post.
		Scanner ReadWords = new Scanner(posts);
		
		int postnum = 0;
		while(SplitPosts.hasNextLine() && postnum < 26)
		{		
			String line = SplitPosts.nextLine();
			FPposts[postnum] = line;//Add the front page posts to the array.  Using an array because we know it will always be 25.

			postnum++;
		}
		
		FPposts[0] = "";//The 0th space is empty because of the way it is parsed.
		
		while(ReadWords.hasNext())//Making a linked list of words.  There is a variable amount (it keeps growing).
		{		
			String line = ReadWords.next();
			line = line.replace(".", "");//Remove periods from ends of sentences, etc.
			line = line.replace(",", "");//Remove periods from ends of sentences, etc.
			FPwords.add(line);//Add the words to the LinkedList.

		}
		
		//TODO: This probably could be done without this, but that's how I did it originally.  Maybe change this later?
		FPwordsArr = FPwords.toArray();//Convert the list to an array.
		FPwordsArrAllTime = FPwords.toArray();
		//Arrays.sort(FPwordsArr);//Perform initial lexicographic sort. 
		//TODO: Doesn't actually need to be sorted, can be changed if performance suffers.
	}
	
	//Outputs the posts on the front page to a text file - will be uploaded later.
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
	
	//Outputs words to a text file(appends). As long as the program is running. 
	//TODO: Find a way to keep the data and use it again so it's not overwritten when restarting.
	public void outputWords() throws IOException
	{

		int count = 0;
		int countAll = 0;
		String temp = "";//Compared to string to remove duplicates in the output.
		String temp2 = "";
		File afpw = new File("ALLFrontPagedWords.txt");
		
		//This might still be useful? Download the file if it's been longer than a certain time...
		//I'll leave it, might be useful.
		
//		if(FileUtils.isFileNewer(afpw, 600000))
//		{
//			URL rwa = new URL("http://www.tcnj.edu/~jamiesj1/rwa/ALLFrontPagedWords.txt");
//			File over = new File("ALLFrontPagedWords.txt");
//			FileUtils.copyURLToFile(rwa, over);
//		}
		
		FileWriter fw = new FileWriter(afpw);
		BufferedWriter bw = new BufferedWriter(fw);
		
		//String filter when adding initial words.
		for(int start = 0; start < FPwordsArr.length; start++)
		{
				//Removes duplicate words in the array.
				for(int rest = start;rest < FPwordsArr.length; rest++)
					if(FPwordsArr[start].toString().equals(FPwordsArr[rest].toString()))
					{
						count++;

						if(temp.equals(FPwordsArr[rest]))
							FPwordsArr[rest] = "";	
						
						temp = FPwordsArr[start].toString();

					}
				//Removes  empty words and spaces.
				if(FPwordsArr[start].equals(""))
				{
				}
				else//Append "Seen # 'word'".
				{
					FPwordsArr[start] = FPwordsArr[start].toString().replaceAll("[^A-Za-z0-9'\\/\\- ]", "");//Remove non-alphanumeric characters.
						if(findStopWords(FPwordsArr[start].toString(),stopWords))//Filter for stop words as of v0.2
						{
							
						}
						else
						{
							//if(count < 500)
								//countAll = count;
							//else countAll += count;
							
							//FPwordsArrAllTime[start] = "Seen " + countAll + " " + FPwordsArr[start].toString();
							if(count > 150)//Reset trending at 100.
								count = 1;
							
							FPwordsArr[start] = "Seen " + count + " " + FPwordsArr[start].toString();
							

						}
					
				
				}
				
				count = 0;
			//}
	
		}	
		
		//String filter when writing to file.  Need both because the first one is add filter, this is write filter.
		for(int write = 0; write < FPwordsArr.length; write++)
		{

							if(findStopWords(FPwordsArr[write].toString(), stopWords))//Filter for stop words.
							{
								
							}
							else
							{
								FPwordsArr[write] = FPwordsArr[write].toString().replaceAll("[^A-Za-z0-9'\\/\\- ]", "");
								bw.append(FPwordsArr[write].toString());
								bw.newLine();
								
							}
			}

		bw.flush();
		bw.close();	
		
		
		
		////////////////
		//TRENDING ABOVE
		/////////////////////////////////////////////////////////////////
		//ALL TIME BELOW
		////////////////
		
		
		
		for(int start = 0; start < FPwordsArrAllTime.length; start++)
		{
				//Removes duplicate words in the array.
				for(int rest = start;rest < FPwordsArrAllTime.length; rest++)
					if(FPwordsArrAllTime[start].toString().equals(FPwordsArrAllTime[rest].toString()))
					{
						countAll++;

						if(temp2.equals(FPwordsArrAllTime[rest]))
							FPwordsArrAllTime[rest] = "";	
						
						temp2 = FPwordsArrAllTime[start].toString();

					}
				//Removes  empty words and spaces.
				if(FPwordsArrAllTime[start].equals(""))
				{
				}
				else//Append "Seen # 'word'".
				{
					FPwordsArrAllTime[start] = FPwordsArrAllTime[start].toString().replaceAll("[^A-Za-z0-9'\\/\\- ]", "");//Remove non-alphanumeric characters.
						if(findStopWords(FPwordsArrAllTime[start].toString(),stopWords))//Filter for stop words as of v0.2
						{
							
						}
						else
						{
							//if(count < 500)
								//countAll = count;
							//else countAll += count;
							
							FPwordsArrAllTime[start] = "Seen " + countAll + " " + FPwordsArrAllTime[start].toString();
							
							//FPwordsArr[start] = "Seen " + count + " " + FPwordsArr[start].toString();
							
							//if(count > 500)//Reset trending at 500.
								//count = 1;
						}
					
				
				}
				
				countAll = 0;
			//}
	
		}	
		//Writing the all time words to a file:
		File alltimewords = new File("AllTimeFPWords.txt");
		FileWriter fw2 = new FileWriter(alltimewords);
		BufferedWriter bw2 = new BufferedWriter(fw2);
		
		for(int write = 0; write < FPwordsArrAllTime.length; write++)
		{

							if(findStopWords(FPwordsArrAllTime[write].toString(), stopWords))//Filter for stop words.
							{
								
							}
							else
							{
								FPwordsArrAllTime[write] = FPwordsArrAllTime[write].toString().replaceAll("[^A-Za-z0-9'\\/\\- ]", "");
								bw2.append(FPwordsArrAllTime[write].toString());
								bw2.newLine();
								
							}
			}

		bw2.flush();
		bw2.close();	
	}
	
	//Matches the posts on the CURRENT front page with the overall(since program started) top words.
	@SuppressWarnings("resource")
	public void matchPosts() throws IOException
	{
		URL origin = new URL("http://www.tcnj.edu/~jamiesj1/rwa/posts.php");//Grab php files containing 25 top words.
		File dest = new File("top25.txt");
		FileUtils.copyURLToFile(origin, dest);
		
		//Place into a string, normalize/remove junk.
		String posts = FileUtils.readFileToString(dest);
		posts = posts.substring(169);
		posts = posts.replace("<pre>", "");
		posts = posts.replace("</pre>", "");
		posts = posts.replace("Seen", "");
		posts = posts.replaceAll("[0-9]", "");
		posts = posts.trim();
		posts = posts.replaceAll(" ", "");
		FileUtils.write(dest, posts);//Output for testing purposes.
		
		Scanner findWords = new Scanner(posts);
		String words[] = new String[25];
		
		int add = 0;
		while(findWords.hasNext())
		{
			words[add] = findWords.next();//Add the 25 words to an array, should always be 25.
			add++;
		}

		//Goes through each word in the posts and tests if it equals a word in the top 25.  Add to new LinkedList if they do.
		for(int matching = 0; matching < FPposts.length-1; matching++)
		{
			Scanner scan = new Scanner(FPposts[matching]);
			while(scan.hasNext())
			{
				String test = scan.next();
				for(int moveWord = 0; moveWord < words.length; moveWord++){
					if(test.equals(words[moveWord]))
					{
						matchingPosts.add(FPposts[matching]);
					}
				}
			}

		}

		//Put the front page posts array into a LinkedList - this is needed to use the retainAll method.
		LinkedList<Object> FPpostsLink = new LinkedList<Object>();
		for(int ar2link = 0; ar2link < FPposts.length; ar2link++)
		{
			FPpostsLink.add(FPposts[ar2link]);
		}

		matchingPosts.retainAll(FPpostsLink);//Only keeps the the posts in matchingPosts that are in FPposts(and FPpostsLink) - that way only current posts will show up.
		FPmatched = matchingPosts.toArray();//Throw matchingPosts into an array to print them out.  Might not be needed?
		
		//Removes duplicate posts in the output.
		String temp = "";
		for(int fix = 0; fix < FPmatched.length; fix++)
		{
			for(int fixLine = fix; fixLine < FPmatched.length; fixLine++)
			{
				if(temp.equals(FPmatched[fixLine]))
					FPmatched[fixLine] = "";
				
				temp = FPmatched[fix].toString();
			}
		}
		
		//Finally write all of this to a file to be uploaded.
		FileWriter fw = new FileWriter("matchedPosts.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		
		Object exact[] = FPmatched.clone();
		
		for(int writePosts = 0; writePosts < FPmatched.length; writePosts++)
		{
			if(FPmatched[writePosts].equals(""))//Don't write if the post is blank.
			{
			}
			else
			{
				
				FPmatched[writePosts] = FPmatched[writePosts].toString().replaceAll("\"", "");
				//Output the string with html in it.  PHP will read this and use it to create the links on the page.
				bw.write("<a href=\"http://www.reddit.com/search?q=" + FPmatched[writePosts].toString() + "\" target=\"_blank\">" + exact[writePosts].toString() + "</a>");
				bw.newLine();
			}
		}
		
		bw.flush();
		bw.close();
	}
	
	//Upload the files through FTP to be read in PHP and displayed.
	public void upload(String server, String username, String password)
	{
		FTPClient ftp = new FTPClient();
		FileInputStream fis = null;
		FileInputStream fis2 = null;
		FileInputStream fis3 = null;
		
		try{
			//Connect using supplied info.
			ftp.connect(server);
			ftp.login(username, password);
			fis = new FileInputStream("ALLFrontPagedWords.txt");//Read this file.
			fis2 = new FileInputStream("matchedPosts.txt");//Read this file.
			fis3 = new FileInputStream("AllTimeFPWords.txt");
			
			ftp.changeWorkingDirectory("www/rwa");//This is the directory I change to.
			ftp.storeFile("ALLFrontPagedWords.txt", fis);//Upload.
			ftp.storeFile("matchedPosts.txt", fis2);//Upload.
			ftp.storeFile("AllTimeFPWords.txt", fis3);//Upload.
			ftp.logout();//IMPORTANT!
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
				ftp.disconnect();//IMPORTANT!
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	//Method to find stop words.
	public static boolean findStopWords(String input, String[] list)
	{
	    for(int word = 0; word < list.length; word++)
	    {
	        if(input.equals(list[word]))
	        {   
	        	//System.out.println(input);
	            return true;
	
	        }
	    }
	    return false;
	}
}
