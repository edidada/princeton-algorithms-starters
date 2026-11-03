# Burrows–Wheeler 实验报告

来源：[Princeton 官方规格](https://coursera.cs.princeton.edu/algs4/assignments/burrows/specification.php)。实现 CircularSuffixArray、BurrowsWheeler、MoveToFront，保留原始文本、二进制文件及官方参考输出，Huffman 完全复用 algs4.jar。分支 edidada/burrows，独立 burrows.yml，JDK17 Maven 多模块及 Gradle。

CircularSuffixArray 使用循环前缀倍增：先按字符计数排序，每轮将长度k的等价类合成长度2k的有序类，利用偏移后的已排序索引和稳定计数排序完成，不复制所有旋转字符串。最坏 O(n log n+R) 时间、O(n+R) 空间；length/index O(1)，支持空串、周期串及UTF-16字符序。main 直接调用全部公开方法。

BWT 输出32位大端 first 和8位末列；空串输出 first=0 的四字节头。逆变换通过稳定计数构建首列和 next 数组，沿 first 回溯恰好 n 个字符，O(n+256) 时间/空间。MTF 对256字节表查询下标并移至首位，编码/解码流式读写 BinaryStdIn/Out；最坏 O(nR) 时间、O(R) 额外空间，重复和近邻字符时接近线性。

远程验收：CSA 三组 JUnit，1000随机串逐个比较完整旋转顺序，官方 ABRACADABRA 索引、null/越界/Unicode、30万字符周期串。Python仅在Actions执行，独立完整旋转/MTF oracle检查真实二进制CLI，涵盖空、全256字节、零/高位字节、周期数据及1MB随机输入；原始abra/aesop/us.gif的BWT和MTF参考文件逐字节比较。aesop英文、JPEG、随机二进制经BWT→MTF→官方Huffman完整往返，并在compression.json记录压缩率、压缩/展开耗时；计时包含每方向三个独立JVM的启动成本。

最终结论以最终提交完成态CI为准，不声称Coursera私有评分。本地未编译或执行实验，仅编辑及静态检查文件。
