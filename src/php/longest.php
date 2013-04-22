<!--
Written by Jack Jamieson
https://twitter.com/jamieson_jack

Reddit Front Page Word Analyzer - longest.php
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

$array = preg_split("/\r\n|\n|\r/", $text);#split by new line and put into an array.

$longarr = str_replace("Seen", "", $array);//All longarr manipulations are to get just the word. Strip away all other parts.

$longarr = array_filter(array_map('trim', $longarr));
$longarr = preg_replace('/\d/', '', $longarr);
$longarr = array_filter(array_map('trim', $longarr));
$longarr = preg_replace("/[^a-zA-Z 0-9 \- \/ \( \)]+/", "", $longarr);

function sortbyL($a,$b){	//Custom sort by string length.  Some problems with UTF-8 encoding, but seems to work for my purposes.
    return strlen($b) - strlen($a);
}

usort($longarr,'sortbyL');

$newarr = array_slice($longarr, 0, 25);#top 25.
$comma = implode("\n", $newarr);#seperate the array back into new lines.

echo '<pre>'; print_r($comma); echo '</pre>'
?>




