# Collinear Points 实验报告

来源：Princeton Algorithms Part I 官方滚动版 collinear starter，保留原始输入文件与 LineSegment。实现 Point 的斜率特殊值、自然序和比较器，以及 O(n^4) 四点枚举和 O(n² log n) 斜率排序算法。Fast 仅由最小端点输出一条最大线段，避免重复和子段；Brute 按题目四元组定义处理，五点以上允许子段。

构建采用 JDK17、Maven 多模块与 Gradle，独立工作流 `.github/workflows/collinear.yml`，所有编译和测试在 GitHub Actions 执行，本地只编辑和检查文件。

验收包含原始 input8 两条线段、空值/重复点异常、防御复制、正零/无穷斜率、七点共线最大性，以及 300 组随机点集对独立整数叉积几何 oracle。尚待最终提交的远程 CI；这些公开测试不等同 Coursera 隐藏评分。
