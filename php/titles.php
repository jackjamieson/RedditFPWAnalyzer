<!--
Written by Jack Jamieson
https://twitter.com/jamieson_jack

Reddit Front Page Word Analyzer - titles.php
Displays the matched posts from the raw text file.!
-->
<?php
$text = file_get_contents('http://www.tcnj.edu/~jamiesj1/rwa/matchedPosts.txt');
$array = preg_split("/\r\n|\n|\r/", $text);#split by new line and put into an array.
$comma = implode("\n", $array);#seperate the array back into new lines.

echo '<pre>'; print_r($comma); echo '</pre>'
?>



