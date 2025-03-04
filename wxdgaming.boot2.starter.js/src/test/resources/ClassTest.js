class ClassTest {

    v1 = 1;

    print() {
        console.log("ClassTest - Hello World");
        jlog.print("1", typeof (1), 1, typeof ("2"), "2");
        jlog.info("ClassTest - Hello World");
    }

    print2(actor) {
        console.log("ClassTest - Hello World - " + actor.uid + " - " + actor);
        jlog.info("ClassTest - Hello World - {} - {} - {} - {}", actor.name, actor.getName(), JSON.stringify(actor.strings), actor);
    }
}