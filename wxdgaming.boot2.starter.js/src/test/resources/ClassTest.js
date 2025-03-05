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

var classTest2 = new ClassTest();

// 扫描实例方法
function scanInstanceMethods(classObj) {
    const instanceMethods = [];
    const prototype = classObj.prototype;
    const properties = Object.getOwnPropertyNames(prototype);

    for (const property of properties) {
        if (typeof prototype[property] === 'function' && property !== 'constructor') {
            instanceMethods.push(property);
        }
    }
    return instanceMethods;
}

// 扫描静态方法
function scanStaticMethods(classObj) {
    const staticMethods = [];
    const properties = Object.getOwnPropertyNames(classObj);

    for (const property of properties) {
        if (typeof classObj[property] === 'function') {
            staticMethods.push(property);
        }
    }
    return staticMethods;
}

function scanGlobalVariables() {
    const globalVars = [];
    // 遍历 globalThis 对象的所有属性
    for (const key in globalThis) {
        const value = globalThis[key];

        if (Object.prototype.hasOwnProperty.call(globalThis, key)) {
            globalVars.push(key);
        }
    }
    return globalVars;
}

// 调用函数进行扫描
const variables = scanGlobalVariables();
console.log('Global variables in GraalVM JavaScript:', variables);
console.log(typeof (classTest2))
var scanInstanceMethods1 = scanInstanceMethods(classTest2.constructor);
console.log('Instance methods:', scanInstanceMethods1);