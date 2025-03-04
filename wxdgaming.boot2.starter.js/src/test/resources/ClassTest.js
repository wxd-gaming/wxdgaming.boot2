class ClassTest {

    v1 = 1;

    print() {
        console.log("ClassTest - Hello World");
        jlog.info("ClassTest - Hello World");
    }

    print2(msg) {
        console.log("ClassTest - Hello World - " + msg.uid + " - " + msg);
        jlog.info("ClassTest - Hello World - " + msg.name + " - " + msg.getName() + " - " + msg);
    }
}