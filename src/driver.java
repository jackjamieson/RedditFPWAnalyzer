//Written by Jack Jamieson
//Reddit Front Page Word Analyzer
//Runs the program.
//Dependencies: jsoup, apache commons net, apache commons io.
//https://twitter.com/jamieson_jack

import java.io.IOException;

public class driver {

	public static void main(String[] args) throws IOException {

		scraper s = new scraper();
		s.execute();

	}

}
