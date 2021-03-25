all:
	javac -Xlint:unchecked -cp ../../lib/json.jar */*.java 

debug:
	javac -cp ../../lib/json.jar */*.java 2>&1 | less

run:
	java -cp ../../lib/json.jar:. ui/Main

clean:
	rm */*.class
