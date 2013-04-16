<!--
Written by Jack Jamieson
https://twitter.com/jamieson_jack

Reddit Front Page Word Analyzer - bottom.php
Displays the bottom 25 words from the raw text file.

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
natsort($array);#perform a natural sort.
$newarr = array_reverse($array, true);#descending order.
$newarr = array_slice($newarr, -26, 26);#bottom 25.
$comma = implode("\n", $newarr);#seperate the array back into new lines.

echo '<pre>'; print_r($comma); echo '</pre>'
?>



