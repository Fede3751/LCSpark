# LCSpark

Submit Example:
	spark-submit --deploy-mode cluster --master spark://address:port --conf "spark.locality.wait.node=0" --conf "spark.executor.extraJavaOptions=-XX:+UseParallelOldGC -XX:ParallelGCThreads=3" --conf spark.memory.fraction=0.1 --executor-memory 5800M --class main.Main '/path/to/package.jar' 'input.txt' 2000 21 1 'output.log' 2>/dev/null
