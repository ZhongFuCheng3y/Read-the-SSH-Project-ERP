---
tags: SSH整合与阅读项目

---


# 前言 #

**本博文主要是记录我阅读过的SSH项目所学习到的知识，并不是相关系列教程**。该SSH项目的gitHub地址：[ERP项目地址](https://github.com/ZhongFuCheng3y/Read-the-SSH-Project-ERP)


# 删除数据 #

实际业务中真正意义上的数据删除操作比较少见，**多数情况是在数据中设置标记，通过标记的值来区分该数据是否可以用，而不是将数据真正的删除**。

根据业务需求，为数据添加标记位，同时对数据的维护中添加启用/停用切换按钮，用于替换删除业务。所有的查询操作默认携带条件值为标记为可以的数据。除特殊业务外，标记为不可用的数据将不参与日常数据操作。


# javaScript数值数据操作 #


**javascript中如果需要对页面组件获取的值进行数字加操作，必须保障两个操作数据都是数字，否则将进行字符串连接运算。此处使用*1操作，将字符串快速转换为数字格式**

# 代码生成器 #

由于我们的Dao、Service、Controller、配置文件都有很多重复的地方，我们可以通过“代码生成器”来将我们的文件生成出来。

- 主要是依靠反射和IO的技术来进行生成对应的文件
- **我个人认为它不够通用、如果使用SSM来进行开发的话，那这段代码的用处就不大了，并且还要遵循它固有的习惯开发。**
- **主要是看看原来还能有这种操作**！

```java

package cn.itcast.erp.util.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cn.itcast.erp.invoice.operdetail.vo.OperDetailModel;

public class GeneratorUtil {
	private Class clazz;
	private String b ;		//Emp
	private String l ;		//e
	private String s ;		//emp
	private String pkg ;	//cn.itcast.erp.auth.emp
	private String dir ;	//cn/itcast/erp/auth/emp/vo
	
	public static void main(String[] args) throws Exception {
		//EmpModel,RoleModel,ResModel,MenuModel
		//SupplierModel,GoodsTypeModel,GoodsModel
		//OrderModel,OrderDetailModel
		//StoreModel,StoreDetailModel,OperDetailModel
		new GeneratorUtil(OperDetailModel.class);
		System.out.println("struts.xml未进行映射");
		System.out.println("HbmXml未添加关联关系");
		System.out.println("QueryModel未添加自定义范围查询条件");
		System.out.println("DaoImpl中未对自定义查询条件形式条件设置");
	}
	
	public GeneratorUtil(Class clazz) throws Exception{
		this.clazz = clazz;
		//生成所有的内容
		//-1.数据初始化
		dataInit();
		//0.创建目录
		generatorDirectory();
		//1.QueryModel
		generatorQueryModel();
		//2.Hbm.xml
		generatorHbmXml();
		//3.Dao
		generatorDao();
		//4.Impl
		generatorImpl();
		//5.Ebi
		generatorEbi();
		//6.Ebo
		generatorEbo();
		//7.Action
		generatorAction();
		//8.applicationContext.xml
		generatorApplicationContextXml();
		//9.struts.xml(选作)
		//modifyStrutsXml();
	}
	
	private void modifyStrutsXml() throws Exception {
		//1.读取原始的内容
		//2.读取到特定位置（package）添加指定内容
		
		//我们要读的文件与写的文件是同一个文件
		/*
		RandomAccessFile类读写文件时
		读取，一共100，读70，写，写的内容会覆盖后30
		111
		222
		333
		444
		在333的后面写5
		111
		222
		333
		544
		在333的后面写5
		111
		222
		333
		555
		*/
		//方案一：
		/*
		读取原始文件，将内容写入新文件
		写之前判断，读取的内容是否是特定内容，特定内容写之前，加入新的内容
		写完毕之后生成了新的文件，删除老的文件，使用新文件更名为老的文件
		*/
		//方案二：
		//1.读取原始文件的文件大小,字节总数1000
		File f = new File("resources/struts.xml");
		long len = f.length();
		//2.创建一个字节数组，大小等于原始文件字节总数
		byte[] buf = new byte[(int)len];
		//3.将原始文件读入该byte数组
		InputStream is = new FileInputStream(f);
		is.read(buf);
		is.close();
		//4.将buf转化为字符串
		String all = new String(buf);
		//5.查找固定位置
		int idx = all.lastIndexOf("    </package>");
		//6.将要写入的内容插入该位置
		String info = "    	<!-- "+b+" -->\r\n    	<action name=\""+s+"_*\" class=\""+s+"Action\" method=\"{1}\">\r\n    	</action>\r\n\r\n";
		//7.将info加入all的指定位置
		StringBuilder sbf = new StringBuilder(all);
		sbf.insert(idx, info);
		//8.将sbf中的组合最终内容写入struts.xml
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(sbf.toString().getBytes());
		fos.close();
	}

	//8.applicationContext.xml
	private void generatorApplicationContextXml() throws Exception {
		File f = new File("resources/applicationContext-"+s+".xml");
		if(f.exists()){
			return;
		}
		f.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bw.newLine();
		
		bw.write("<beans xmlns=\"http://www.springframework.org/schema/beans\"");
		bw.newLine();
		
		bw.write("	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		bw.newLine();
		
		bw.write("	xsi:schemaLocation=\"");
		bw.newLine();
		
		bw.write("		http://www.springframework.org/schema/beans ");
		bw.newLine();
		
		bw.write("		http://www.springframework.org/schema/beans/spring-beans.xsd");
		bw.newLine();
		
		bw.write("		\"> ");
		bw.newLine();
		
		bw.write("	<!-- Action -->");
		bw.newLine();
		
		bw.write("	<bean id=\""+s+"Action\" class=\""+pkg+".web."+b+"Action\" scope=\"prototype\">");
		bw.newLine();
		
		bw.write("		<property name=\""+s+"Ebi\" ref=\""+s+"Ebi\"/>");
		bw.newLine();
		
		bw.write("	</bean>");
		bw.newLine();
		
		bw.write("	<!-- Ebi -->");
		bw.newLine();
		
		bw.write("	<bean id=\""+s+"Ebi\" class=\""+pkg+".business.ebo."+b+"Ebo\">");
		bw.newLine();
		
		bw.write("		<property name=\""+s+"Dao\" ref=\""+s+"Dao\"/>");
		bw.newLine();
		
		bw.write("	</bean>");
		bw.newLine();
		
		bw.write("	<!-- Dao -->");
		bw.newLine();
		
		bw.write("	<bean id=\""+s+"Dao\" class=\""+pkg+".dao.impl."+b+"Impl\">");
		bw.newLine();
		
		bw.write("		<property name=\"sessionFactory\" ref=\"sessionFactory\"/>");
		bw.newLine();
		
		bw.write("	</bean>");
		bw.newLine();
		
		bw.write("</beans>");
		bw.newLine();
		
		bw.flush();
		bw.close();			
	}

	//7.Action
	private void generatorAction() throws Exception {
		File f = new File("src/"+dir+"/web/"+b+"Action.java");
		if(f.exists()){
			return;
		}
		f.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		bw.write("package "+pkg+".web;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("import java.util.List;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("import "+pkg+".business.ebi."+b+"Ebi;");
		bw.newLine();
		
		bw.write("import "+pkg+".vo."+b+"Model;");
		bw.newLine();
		
		bw.write("import "+pkg+".vo."+b+"QueryModel;");
		bw.newLine();
		
		bw.write("import cn.itcast.erp.util.base.BaseAction;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("public class "+b+"Action extends BaseAction{");
		bw.newLine();
		
		bw.write("	public "+b+"Model "+l+"m = new "+b+"Model();");
		bw.newLine();
		
		bw.write("	public "+b+"QueryModel "+l+"qm = new "+b+"QueryModel();");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	private "+b+"Ebi "+s+"Ebi;");
		bw.newLine();
		
		bw.write("	public void set"+b+"Ebi("+b+"Ebi "+s+"Ebi) {");
		bw.newLine();
		
		bw.write("		this."+s+"Ebi = "+s+"Ebi;");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	//列表");
		bw.newLine();
		
		bw.write("	public String list(){");
		bw.newLine();
		
		bw.write("		setDataTotal("+s+"Ebi.getCount("+l+"qm));");
		bw.newLine();
		
		bw.write("		List<"+b+"Model> "+s+"List = "+s+"Ebi.getAll("+l+"qm,pageNum,pageCount);");
		bw.newLine();
		
		bw.write("		put(\""+s+"List\", "+s+"List);");
		bw.newLine();
		
		bw.write("		return LIST;");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	//到添加");
		bw.newLine();
		
		bw.write("	public String input(){");
		bw.newLine();
		
		bw.write("		if("+l+"m.getUuid()!=null){");
		bw.newLine();
		
		bw.write("			"+l+"m = "+s+"Ebi.get("+l+"m.getUuid());");
		bw.newLine();
		
		bw.write("		}");
		bw.newLine();
		
		bw.write("		return INPUT;");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	//添加");
		bw.newLine();
		
		bw.write("	public String save(){");
		bw.newLine();
		
		bw.write("		if("+l+"m.getUuid() == null){");
		bw.newLine();
		
		bw.write("			"+s+"Ebi.save("+l+"m);");
		bw.newLine();
		
		bw.write("		}else{");
		bw.newLine();
		
		bw.write("			"+s+"Ebi.update("+l+"m);");
		bw.newLine();
		
		bw.write("		}");
		bw.newLine();
		
		bw.write("		return TO_LIST;");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	//删除");
		bw.newLine();
		
		bw.write("	public String delete(){");
		bw.newLine();
		
		bw.write("		"+s+"Ebi.delete("+l+"m);");
		bw.newLine();
		
		bw.write("		return TO_LIST;");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("}");
		bw.newLine();

		bw.flush();
		bw.close();			
	}

	//6.Ebo
	private void generatorEbo()  throws Exception {
		File f = new File("src/"+dir+"/business/ebo/"+b+"Ebo.java");
		if(f.exists()){
			return;
		}
		f.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		bw.write("package "+pkg+".business.ebo;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("import java.io.Serializable;");
		bw.newLine();
		
		bw.write("import java.util.List;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("import "+pkg+".business.ebi."+b+"Ebi;");
		bw.newLine();
		
		bw.write("import "+pkg+".dao.dao."+b+"Dao;");
		bw.newLine();
		
		bw.write("import "+pkg+".vo."+b+"Model;");
		bw.newLine();
		
		bw.write("import cn.itcast.erp.util.base.BaseQueryModel;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("public class "+b+"Ebo implements "+b+"Ebi{");
		bw.newLine();
		
		bw.write("	private "+b+"Dao "+s+"Dao;");
		bw.newLine();
		
		bw.write("	public void set"+b+"Dao("+b+"Dao "+s+"Dao) {");
		bw.newLine();
		
		bw.write("		this."+s+"Dao = "+s+"Dao;");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	public void save("+b+"Model "+l+"m) {");
		bw.newLine();
		
		bw.write("		"+s+"Dao.save("+l+"m);");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	public void update("+b+"Model "+l+"m) {");
		bw.newLine();
		
		bw.write("		"+s+"Dao.update("+l+"m);");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	public void delete("+b+"Model "+l+"m) {");
		bw.newLine();
		
		bw.write("		"+s+"Dao.delete("+l+"m);");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	public "+b+"Model get(Serializable uuid) {");
		bw.newLine();
		
		bw.write("		return "+s+"Dao.get(uuid);");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	public List<"+b+"Model> getAll() {");
		bw.newLine();
		
		bw.write("		return "+s+"Dao.getAll();");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	public List<"+b+"Model> getAll(BaseQueryModel qm, Integer pageNum,Integer pageCount) {");
		bw.newLine();
		
		bw.write("		return "+s+"Dao.getAll(qm,pageNum,pageCount);");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	public Integer getCount(BaseQueryModel qm) {");
		bw.newLine();
		
		bw.write("		return "+s+"Dao.getCount(qm);");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("}");
		bw.newLine();
		
		bw.flush();
		bw.close();			
	}

	//5.Ebi
	private void generatorEbi()  throws Exception {
		File f = new File("src/"+dir+"/business/ebi/"+b+"Ebi.java");
		if(f.exists()){
			return;
		}
		f.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		bw.write("package "+pkg+".business.ebi;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("import org.springframework.transaction.annotation.Transactional;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("import "+pkg+".vo."+b+"Model;");
		bw.newLine();
		
		bw.write("import cn.itcast.erp.util.base.BaseEbi;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("@Transactional");
		bw.newLine();
		
		bw.write("public interface "+b+"Ebi extends BaseEbi<"+b+"Model>{");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("}");
		bw.newLine();
		
		bw.flush();
		bw.close();	
	}

	//4.Impl
	private void generatorImpl()  throws Exception {
		File f = new File("src/"+dir+"/dao/impl/"+b+"Impl.java");
		if(f.exists()){
			return;
		}
		f.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		bw.write("package "+pkg+".dao.impl;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("import org.hibernate.criterion.DetachedCriteria;");
		bw.newLine();

		bw.write("import org.hibernate.criterion.Restrictions;");
		bw.newLine();

		bw.newLine();
		
		bw.write("import "+pkg+".dao.dao."+b+"Dao;");
		bw.newLine();
		
		bw.write("import "+pkg+".vo."+b+"Model;");
		bw.newLine();
		
		bw.write("import "+pkg+".vo."+b+"QueryModel;");
		bw.newLine();
		
		bw.write("import cn.itcast.erp.util.base.BaseImpl;");
		bw.newLine();
		
		bw.write("import cn.itcast.erp.util.base.BaseQueryModel;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("public class "+b+"Impl extends BaseImpl<"+b+"Model> implements "+b+"Dao{");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("	public void doQbc(DetachedCriteria dc,BaseQueryModel qm){");
		bw.newLine();
		
		bw.write("		"+b+"QueryModel "+l+"qm = ("+b+"QueryModel)qm;");
		bw.newLine();
		
		bw.write("		// TODO 添加自定义查询条件");
		bw.newLine();
		
		bw.write("	}");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("}");
		bw.newLine();
		
		bw.flush();
		bw.close();		
	}

	//3.Dao
	private void generatorDao() throws Exception {
		File f = new File("src/"+dir+"/dao/dao/"+b+"Dao.java");
		if(f.exists()){
			return;
		}
		f.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		bw.write("package "+pkg+".dao.dao;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("import "+pkg+".vo."+b+"Model;");
		bw.newLine();
		
		bw.write("import cn.itcast.erp.util.base.BaseDao;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("public interface "+b+"Dao extends BaseDao<"+b+"Model> {");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("}");
		bw.newLine();
		
		bw.flush();
		bw.close();		
	}

	//2.Hbm.xml
	private void generatorHbmXml() throws Exception {
		//1.创建文件
		File f = new File("src/"+dir+"/vo/"+b+"Model.hbm.xml");
		
		if(f.exists()){
			return;
		}
		
		f.createNewFile();
		//2.IO写入内容
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bw.newLine();
		
		bw.write("<!DOCTYPE hibernate-mapping PUBLIC");
		bw.newLine();
		
		bw.write("        '-//Hibernate/Hibernate Mapping DTD 3.0//EN'");
		bw.newLine();
		
		bw.write("        'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>");
		bw.newLine();
		
		bw.write("<hibernate-mapping>");
		bw.newLine();
		
		bw.write("    <class name=\""+pkg+".vo."+b+"Model\" table=\"tbl_"+s+"\">");
		bw.newLine();
		
		bw.write("        <id name=\"uuid\">");
		bw.newLine();
		
		bw.write("            <generator class=\"native\" />");
		bw.newLine();
		
		bw.write("        </id>");
		bw.newLine();
		
		//hibernate的映射配置文件中要对原始模型类中的属性进行配置，反射获取所有字段
		Field[] fds = clazz.getDeclaredFields();
		for(Field fd:fds) {
			//如果字段的修饰符是private，生成
			if(fd.getModifiers() == Modifier.PRIVATE && !fd.getName().equals("uuid")){
				//如果是关联关系不生成,不是关联关系(Long,Integer,Double,String)
				if( fd.getType().equals(String.class)||
					fd.getType().equals(Long.class)||
					fd.getType().equals(Integer.class)||
					fd.getType().equals(Double.class)
					){
					if(!fd.getName().endsWith("View")){
						bw.write("        <property name=\""+fd.getName()+"\"/>");
						bw.newLine();
					}
				}
			}
		}
		
		bw.write("    </class>");
		bw.newLine();
		
		bw.write("</hibernate-mapping>");
		bw.newLine();
		
		bw.flush();
		bw.close();		
	}

	//1.QueryModel
	private void generatorQueryModel() throws Exception {
		//1.创建文件
		File f = new File("src/"+dir+"/vo/"+b+"QueryModel.java");
		
		//判断：如果该文件存在，终止操作
		if(f.exists()){
			return;
		}
		
		f.createNewFile();
		//2.IO写入内容
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		bw.write("package "+pkg+".vo;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("import cn.itcast.erp.util.base.BaseQueryModel;");
		bw.newLine();
		
		bw.newLine();
		
		bw.write("public class "+b+"QueryModel extends "+b+"Model implements BaseQueryModel{");
		bw.newLine();
		
		bw.write("	// TODO 添加自定义查询条件");
		bw.newLine();
		
		bw.write("}");
		bw.newLine();
		
		bw.flush();
		bw.close();
	}
	
	//0.创建目录
	private void generatorDirectory() {
		//business/ebi
		//				 src+//cn.itcast.erp.auth.emp+business/ebi .vo
		File f = new File("src/"+dir+"/business/ebi");
		f.mkdirs();
		//business/ebo
		f = new File("src/"+dir+"/business/ebo");
		f.mkdirs();
		//dao/dao
		f = new File("src/"+dir+"/dao/dao");
		f.mkdirs();
		//dao/impl
		f = new File("src/"+dir+"/dao/impl");
		f.mkdirs();
		//web
		f = new File("src/"+dir+"/web");
		f.mkdirs();
	}
	
	//-1.数据初始化
	private void dataInit() {
		String className = clazz.getSimpleName();					//EmpModel
		b = className.substring(0, className.length()-5);	//Emp
		String first = b.substring(0,1);							//E
		l = first.toLowerCase();						//e
		s = l+b.substring(1);						//emp
		String rootPkg = clazz.getPackage().getName();				//cn.itcast.erp.auth.emp.vo
		pkg = rootPkg.substring(0,rootPkg.length()-3);		//cn.itcast.erp.auth.emp
		dir = pkg.replace(".","/");							//cn/itcast/erp/auth/emp/vo
	}
	
	/*
	public static void main(String[] args) throws Exception {
		//核心工作原理：文件IO+反射
		File f = new File("src/EmpAction.java");
		f.createNewFile();
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write("public class EmpAction{}");
		bw.flush();
		bw.close();
	}
	*/
}
```

# 拦截器捕获异常 #

一般地，我们都是Dao、servcie层都把异常抛出，最后由Action来进行捕获。如果所有的Action都要捕获异常的话，那么就多了很多的重复代码。

因此，我们**可以使用拦截器来进行统一捕获**。


# 修改角色权限无法同步的问题 #



**如果用户已经登录，此时修改登录用户的角色信息或角色对应的资源信息。由于用户的数据已经存在于Session中，无法及时进行数据同步。**


解决方案：


**在ServletContext范围内**设置集合变量changeEmp,当管理员维护到某个员工的角色时，将该员工的OID放入该集合；

当管理员维护到某个角色的资源时，将该角色对应的所有员工OID放入该集合。

在登录校验时，获取完登录信息后，再次获取该集合，查看当前用户OID是否存在于该集合。如果存在于该集合中，清空当前登录人信息，将员工OID从集合中删除，同时跳转到登录界面。

- **将要修改的用户保存到集合中（记录被修改的用户）**
- **等到校验的时候发现该用户是否被修改过（在集合中是否存在），如果存在，那么则强制注销，跳转回登陆页面**


# 配置数据源 #

一般地，我们做开发都是直接使用tomcat来连接数据库，**但是如果数据库的地址变化的时候，我们就要手动去修改连接的地址了。这样会造成一些不必要的麻烦。**

![](http://images2017.cnblogs.com/blog/1053130/201710/1053130-20171023193807316-2003518.png)


所以，**官方是推荐我们使用JNDI来配置数据源，使用JNDI来连接数据库的。这样即使数据库的地址变了，我们就不用手动去修改连接的地址。**

步骤如下：


-	**修改tomcat安装目录/conf/context.xml，添加如下配置**
```xml


	<Resource 
		name="jdbc/DataSource" 
		auth="Container" 
		type="javax.sql.DataSource"
		maxActive="100" 
		maxIdle="30" 
		maxWait="10000"
		url="jdbc:mysql://localhost:3306/erpdb"
		driverClassName="com.mysql.jdbc.Driver"
		username="root" 
		password="root" 
	/>
```

- **配置Web应用使用Tomcat数据源,修改WEB-INF/web.xml，添加如下配置**


```xml


	<resource-ref>
		<res-ref-name>jdbc/DataSource</res-ref-name>
		<res-type>javax.sql.DataSource</res-type>
		<res-auth>Container</res-auth>
	</resource-ref>
```

- 接着来spring中配置我们的数据源，是从JNDI从查找出来的。

![](http://images2017.cnblogs.com/blog/1053130/201710/1053130-20171023194042785-1457421065.png)


**JNDI的命名，不同web服务器有不同的书写格式：**

```
JNDI名是远端访问的通用名称，可以理解为使用该名称获取对应的资源信息。
JNDI的命名官方规范		java:comp/env/JNDI名称
Jboss	java:JNDI名称
Weblogic	JNDI名称

```


# 权限控制菜单树 #

每个菜单树需要的权限可能是不一样的，要想用权限系统去管理菜单树，就要把菜单树写成是动态的。

![](http://images2017.cnblogs.com/blog/1053130/201710/1053130-20171023195714910-1227881192.png)

菜单模型设计如下：

- **菜单名称，菜单编号，所属父级菜单对象，菜单对应访问路径**


**在查询的时候用到了自关联**


![](http://images2017.cnblogs.com/blog/1053130/201710/1053130-20171024081923660-380625900.png)

----------

# 商品联动 #

![](http://images2017.cnblogs.com/blog/1053130/201710/1053130-20171024082552754-1107726095.png)

在页面加载的时候，查询出**所有供应商的数据、查询第一个供应商商品的类别以及第一个商品类别对应的所有信息集合。**那么在页面上我们就可以将其展示出来了。


随后如果发生了改变的话，那么还是使用ajax来进行联动。


**在添加新项的时候，对应的联动还是需要保持的，因此需要在外面套一层ajax来获取具体的数据！**

![](http://images2017.cnblogs.com/blog/1053130/201710/1053130-20171024085435238-1133095762.png)

```java

	//吐槽一下，这代码看得我贼鸡儿恶心，逻辑是这样子的：

	//首先在新增的时候我们要新增出新行的页面。
	//同时下拉框的数据需要与数据库后台进行联动
	//并且只能动态添加不同名称的商品，类别下所有商品已经存在了，那么就不能选择该类别了。
	//还要对其不能随便修改，否则相同类型相同名称的商品又存在了。
	// HTML+Jquery的拼接看得十分蛋疼....

```

```javascript


//添加新商品
        $("#add").click(function () {
            //设置不能修改供应商
            $("#supplier").attr("disabled", "disabled");
            //设置已存在的所有select全部不可点击
            $(".goodsType").attr("disabled", "disabled");
            $(".goods").attr("disabled", "disabled");

            //发送ajax提交，将供应商id与当前已经使用的类别对应商品2个id传递到后台，并将其过滤，过滤完毕的数据传递回页面
            var goodsTypeObjs = $(".goodsType");
            var goodsObjs = $(".goods");
            var jsonParam = {"gm.goodsType.supplier.uuid": $("#supplier").val()};
            var hasUuids = "";
            for (i = 0; i < goodsTypeObjs.length; i++) {
                hasUuids = hasUuids + $(goodsTypeObjs[i]).val();
                hasUuids = hasUuids + ":";
                hasUuids = hasUuids + $(goodsObjs[i]).val();
                if (i != goodsTypeObjs.length - 1) {
                    hasUuids = hasUuids + ",";
                }
            }
            jsonParam["hasUuids"] = hasUuids;

            //动态添加一个tr行
            //创建tr组件
            var $tr = $("<tr></tr>");

            //创建td组件，生成商品类别select
            var typeSelectStr = "<select class='goodsType' style='width:200px'>";
            for (i = 0; i < 4; i++) {
                typeSelectStr += "<option value='";
                typeSelectStr += 111;
                typeSelectStr += "'>";
                typeSelectStr += "类别名称" + i;
                typeSelectStr += "</option>";
            }
            typeSelectStr += "</select>";
            var $td1 = $("<td height='30'>" + typeSelectStr + "</td>");
            $tr.append($td1);

            //创建td组件，生成商品select
            var goodsSelectStr = "<select name='gmUuids' class='goods' style='width:200px'>";
            for (i = 0; i < 4; i++) {
                goodsSelectStr += "<option value='";
                goodsSelectStr += 123;
                goodsSelectStr += "'>";
                goodsSelectStr += "新商品名" + i;
                goodsSelectStr += "</option>";
            }
            goodsSelectStr += "</select>";
            var $td2 = $("<td>" + goodsSelectStr + "</td>");
            $tr.append($td2);

            //创建td组件，生成商品数量input，默认值1
            var $td3 = $("<td>&nbsp;<input name='nums' type='text' value='1' class='num order_num' style='width:67px;border:1px solid black;text-align:right;padding:2px' /></td>");
            $tr.append($td3);

            var $td4 = $("<td style='padding-left:4px'><input name='prices' type='text' value='" + 222 + "' class='prices order_num' style='width:93px;border:1px solid black;text-align:right;padding:2px' /> 元</td>");
            $tr.append($td4);

            var $td5 = $("<td class='total' align='right'>" + 222 + " 元</td>");
            $tr.append($td5);

            var $td6 = $("<td>&nbsp;&nbsp;<a class='deleteBtn delete xiu' value='" + 112 + "'><img src='../../../images/icon_04.gif'/> 删除</a></td>");
            $tr.append($td6);

            //在最后一个tr对象前添加该tr组件
            $("#finalTr").before($tr);
            //注意：新添加的元素是否具有动态事件激活能力

            //获取后台数据是否还有下一个可用的商品，判断添加按钮是否显示
            if ("Y" == "N") {
                //将添加按钮设置为不可显示
                $("#add").css("display", "none");
            }
            total();
        });
```

![](http://images2017.cnblogs.com/blog/1053130/201710/1053130-20171024095230941-1650365096.png)

可以使用两种方式来进行控制数据：

**SQL控制：**
```sql

//获取商品类别信息，类别必须有商品，企业ID=1,过滤掉1,2商品

select distinct
	gt.*
from
	tbl_goodsType gt,
	tbl_goods g
where
	gt.supplierUuid = 1
and
	g.goodsTypeUuid = gt.uuid
and
	g.uuid not in (1,2,3,4,5)

//过滤使用过的商品，2号类别，过滤掉1,2,3,4商品
select
	*
from 
	tbl_goods
where
	goodsTypeUuid = 2
and
	uuid  not in (1,2,3,4)
```

程序控制：

```java

	//已经使用过的，需要过滤的商品uuid
	public String used;			//'11','22','33','44',
	//ajax根据供应商的uuid获取类别和商品信息(具有数据过滤功能)
	public String ajaxGetGtmAndGm2(){
		gtmList = goodsTypeEbi.getAllUnionBySm(supplierUuid);
		
		//由于类别数据中第一个类别的所有商品已经使用完毕，而没有将其删除，导致该商品类别对应的商品集合在下面的迭代过程中没有商品，抛出索引越界异常
		//解决方案：删除掉对应的商品类别即可
		//过滤掉所有商品已经使用完毕的商品类别
		Goods:
		for(int i = gtmList.size()-1 ;i>=0;i--){
			List<GoodsModel> gmListTemp = goodsEbi.getAllByGtm(gtmList.get(i).getUuid());
			for(GoodsModel gmTemp : gmListTemp){
				if(!used.contains("'"+gmTemp.getUuid()+"'")){
					//保留该类别，直接判断下一个类别
					continue Goods;
				}
			}
			//循环执行完毕，执行到的此处，上述循环没有进入if语句，里面的商品都使用过
			gtmList.remove(i);
		}
		//上述集合中的类别一定都存在没有使用过的商品
		
		gmList = goodsEbi.getAllByGtm(gtmList.get(0).getUuid());
		//当前获取的商品信息的uuid具有重复的，要对其进行过滤
		//对集合进行迭代删除，怎么做？必须逆向进行
		//从gmList中取出所有的元素，挨个迭代，与本次传递过来的used进行比对，比对完发现重复的，删除掉（逆序进行）
		for(int i = gmList.size()-1 ;i >=0 ;i--){
			Long uuid = gmList.get(i).getUuid();
			//判断该uuid是否出现在used中
			if(used.contains("'"+uuid+"'")){
				gmList.remove(i);
			}
		}
		gm = gmList.get(0);
		return "ajaxGetGtmAndGm";
	}
	
	//ajax根据商品类别uuid获取商品信息
	public String ajaxGetGm(){
		gmList = goodsEbi.getAllByGtm(gtmUuid);
		//过滤数据
		for(int i = gmList.size()-1 ;i >=0 ;i--){
			Long uuid = gmList.get(i).getUuid();
			//判断该uuid是否出现在used中
			if(used.contains("'"+uuid+"'")){
				gmList.remove(i);
			}
		}
		gm = gmList.get(0);
		return "ajaxGetGm";
	}

```

# Juqery未来对象 #

如果在生成的代码不是当前对象的，那么绑定的事件就无法被激活了。**因此需要使用live去激活未来对象**。

![](http://images2017.cnblogs.com/blog/1053130/201710/1053130-20171024101231707-1045482792.png)


![](http://images2017.cnblogs.com/blog/1053130/201710/1053130-20171024105455566-2074015791.png)

2018年3月11日18:01:31更新！

**Jquery高版本已经舍弃这个方法了，改成了on方法**

# 订单设计 #

我们的订单可能分很多种：**采购订单、销售订单、采购退货单、销售退货单**

一般地，我们都不会将它们四种都用字段来表示出来，**仅仅使用订订单状态字段进行了区分**
