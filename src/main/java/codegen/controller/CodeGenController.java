package codegen.controller;

import com.jfinal.template.Engine;
import com.jfinal.template.Template;

import diagram.Diagram;

import com.jfinal.kit.Kv;

public class CodeGenController {
	
	public void test(Diagram pDiagram) {
		Engine engine = Engine.use();

		engine.setDevMode(true);
		engine.setToClassPathSourceFactory();
		engine.addSharedObject("attrSeg", new String[2]);
		Template tem = engine.getTemplate("ClassTemplate.txt");
//		if(pDiagram.rootNodes().get(0) == null) {
//			System.out.println("diagram null error");
//			System.exit(1);
//		}
//		Kv kv = Kv.by("pClass", pDiagram.rootNodes().get(0));
		engine.addSharedObject("pClass", pDiagram.getProtoTypes().get(0));
		Kv kv = Kv.by("metamodelname", pDiagram.getName());
		String str = tem.renderToString(kv);
		System.out.println(str);
	}
}
