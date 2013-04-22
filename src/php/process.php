<!--
Written by Jack Jamieson
https://twitter.com/jamieson_jack

Reddit Front Page Word Analyzer - process.php
Displays the top 25 words from the raw text file.

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
-->
<?php
$text = file_get_contents('http://www.tcnj.edu/~jamiesj1/rwa/ALLFrontPagedWords.txt');
$array = preg_split("/\r\n|\n|\r/", $text);

$sentData = $_POST['title'];#Data from the index page.

$searchthis = $sentData;
$matches = array();
$ignore = "Seen";

$handle = @fopen("http://www.tcnj.edu/~jamiesj1/rwa/ALLFrontPagedWords.txt", "r");#Open the file, read-only.
if ($handle)
{
    while (!feof($handle))
    {
        $buffer = fgets($handle);
        if(strpos($buffer, $searchthis) !== FALSE)
			if($searchthis == $ignore)#Don't allow users to type Seen...it's part of all strings.
			{
				echo("Invalid word.");
				break;
			}
			else{
				$matches[] = $buffer;
				}
    }
    fclose($handle);
}
natsort($matches);#Show the highest at the top.
$matches = array_reverse($matches, false);#descending order.
if(empty($matches))
{
	echo '<pre>'."No matches found.".'</pre>';
}

$newarr = array_slice($matches, 0, 5);#show only 5 results.
$comma = implode("\n", $newarr);#seperate the array back into new lines.

echo '<pre>'; print_r($comma); echo '</pre>'
?>



