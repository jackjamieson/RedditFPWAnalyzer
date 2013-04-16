<!--
Written by Jack Jamieson
https://twitter.com/jamieson_jack

Reddit Front Page Word Analyzer - posts.php
Displays the top 25 words from the raw text file.!
-->
<?php
$text = file_get_contents('http://www.tcnj.edu/~jamiesj1/rwa/ALLFrontPagedWords.txt');

$array = preg_split("/\r\n|\n|\r/", $text);#split by new line and put into an array.
natsort($array);#perform a natural sort.
$newarr = array_reverse($array, true);#descending order.
$newarr = array_slice($newarr, 0, 25);#top 25.
$comma = implode("\n", $newarr);#seperate the array back into new lines.

echo '<pre>'; print_r($comma); echo '</pre>'
?>




