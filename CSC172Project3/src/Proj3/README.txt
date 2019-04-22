Name: Ziyi You
Assignment number: Project 3 Huffman coding
NetID: zyou5
Lab section: MW 615 730

In this project, I implemented the Huffman coding algorithm, which reads in files and compress them losslessly. I started by generating a frequency file which counts the occurence of each character in the file.

There are two parts of huffman algorithm: encode and decode. In the first part, I assigned huffman code to characters and the lengths of the assigned codes are based on the frequencies of corresponding characters. The most frequent character gets the smallest code and the least frequent character gets the largest code.

In the second part, I read the frequency file and build a huffman tree using information from the frequency file. Then I read the encoded file and decode by reading each boolean value and traversing down the huffman tree.
