package com.mwy.tool;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ClassInfoUtil {
    public static final String INTERFACE = "I";

    public static ClassInfo parseClass(String file, Function<String, Boolean> fun) throws Exception {
        String[] split = file.split("\\.");
        return parseClass(ClassInfoUtil.class.getResourceAsStream("/" + file.replace(".", "/") + ".class"), split[split.length - 1] + ".class", fun);
    }

    public static ClassInfo parseClass(Class clazz, Function<String, Boolean> fun) throws Exception {
        String filePath = clazz.getName().replace(".", "/") + ".class";
        String fileName = clazz.getSimpleName() + ".class";
        return parseClass(ClassInfoUtil.class.getResourceAsStream("/" + filePath), fileName, fun);
    }

    public static ClassInfo parseClass(InputStream in, String fileName, Function<String, Boolean> fun) throws Exception {
        ClassParser classParser = new ClassParser(in, fileName);
        JavaClass parse = classParser.parse();

        JavaClass[] superClasses = parse.getSuperClasses();

        Method[] methods = parse.getMethods();
        List<MethodInfo> methodInfos = new ArrayList<>();
        for (Method method : methods) {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setClassName(parse.getClassName());
            methodInfo.setMethodName(method.getName());
            methodInfo.setArguments(argumentList(method.getArgumentTypes()));
            if(method.isInterface()){
                methodInfo.setType("I");
            }else if(method.isStatic()){
                methodInfo.setType("S");
            }else{
                methodInfo.setType("M");
            }

            if (!fun.apply(methodInfo.getClassName()+","+methodInfo.getMethodName())) {
                continue;
            }

            MethodGen methodGen = new MethodGen(method, parse.getClassName(), new ConstantPoolGen(parse.getConstantPool()));
            if (methodGen.isAbstract() || methodGen.isNative()) {
                continue;
            }

            ConstantPoolGen constantPoolGen = methodGen.getConstantPool();
            InstructionHandle instructionHandle = methodGen.getInstructionList().getStart();
            while (instructionHandle != null) {
                instructionHandle.getInstruction().accept(new EmptyVisitor() {

                    @Override
                    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
                        if (!fun.apply(i.getReferenceType(constantPoolGen).toString()+"," + i.getMethodName(constantPoolGen))) {
                            return;
                        }
                        methodInfo.getNextCalls().add(new MethodInfo("M", i.getReferenceType(constantPoolGen).toString(), i.getMethodName(constantPoolGen), argumentList(i.getArgumentTypes(constantPoolGen)),i.getReturnType(constantPoolGen).toString(), null));
                    }

                    @Override
                    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
                        if (!fun.apply(i.getReferenceType(constantPoolGen).toString()+"," + i.getMethodName(constantPoolGen))) {
                            return;
                        }
                        methodInfo.getNextCalls().add(new MethodInfo("I", i.getReferenceType(constantPoolGen).toString(), i.getMethodName(constantPoolGen), argumentList(i.getArgumentTypes(constantPoolGen)), i.getReturnType(constantPoolGen).toString(),null));
                    }

                    @Override
                    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
                        if (!fun.apply(i.getReferenceType(constantPoolGen).toString()+"," + i.getMethodName(constantPoolGen))) {
                            return;
                        }
                        methodInfo.getNextCalls().add(new MethodInfo("O", i.getReferenceType(constantPoolGen).toString(), i.getMethodName(constantPoolGen), argumentList(i.getArgumentTypes(constantPoolGen)), i.getReturnType(constantPoolGen).toString(),null));
                    }

                    @Override
                    public void visitINVOKESTATIC(INVOKESTATIC i) {
                        if (!fun.apply(i.getReferenceType(constantPoolGen).toString()+"," + i.getMethodName(constantPoolGen))) {
                            return;
                        }
                        methodInfo.getNextCalls().add(new MethodInfo("S", i.getReferenceType(constantPoolGen).toString(), i.getMethodName(constantPoolGen), argumentList(i.getArgumentTypes(constantPoolGen)), i.getReturnType(constantPoolGen).toString(),null));
                    }

                    @Override
                    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
                        if (!fun.apply(i.getType(constantPoolGen).toString()+"," + i.getMethodName(constantPoolGen))) {
                            return;
                        }

                        methodInfo.getNextCalls().add(new MethodInfo("D", i.getType(constantPoolGen).toString(), i.getMethodName(constantPoolGen), argumentList(i.getArgumentTypes(constantPoolGen)),i.getReturnType(constantPoolGen).toString(), null));
                    }
                });

                instructionHandle = instructionHandle.getNext();
            }

            methodInfos.add(methodInfo);
        }
        ClassInfo classInfo = new ClassInfo();
        classInfo.setClassName(parse.getClassName());
        classInfo.setMethodInfos(methodInfos);
        return classInfo;
    }

    private static String argumentList(Type[] arguments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(arguments[i].toString());
        }
        return sb.toString();
    }

    public static List<MethodInfo> getMethodChain(String className,Function<String, Boolean> fun,Function<String,String> implFun) throws Exception {
        ClassInfo classInfo = parseClass(className, fun);
        ClassContext.getInstance().put(className,classInfo);
        List<MethodInfo> methodInfos = classInfo.getMethodInfos();
        Set<MethodInfo> set = new HashSet<>();
        methodInfos.stream().flatMap(e->e.getNextCalls().stream()).forEach(e->{
            set.add(e);
            e.setNextCalls(getMethod(e,fun,implFun,set));
            set.remove(e);
        });
        return methodInfos;
    }
    public static List<MethodInfo> getMethodChain(String className,Function<String, Boolean> fun) throws Exception {
        return getMethodChain(className,fun,e->{
            String[] split = e.split("\\.");
            split[split.length-2] = split[split.length-2] + ".impl";
            return String.join(".",split) + "Impl";
        });
    }

    public static List<MethodInfo> getMethod(MethodInfo methodInfo, Function<String, Boolean> fun, Function<String, String> implFun, Set<MethodInfo> set) {
        if(methodInfo == null){
            return null;
        }
        String className = methodInfo.getClassName();
        if(INTERFACE.equals(methodInfo.getType()) && implFun != null){
               className = implFun.apply(methodInfo.getClassName());
        }

        ClassInfo classInfo = null;
        try {
            classInfo = ClassContext.getInstance().get(className);
            if(classInfo == null){
                classInfo = parseClass(className, fun);
                ClassContext.getInstance().put(className,classInfo);
            }
        } catch (Exception e) {
            return null;
        }

        MethodInfo resultMethod = classInfo.getMethodInfos().stream().filter(e -> {
            return  e.getMethodName().equals(methodInfo.getMethodName()) && e.getArguments().equals(methodInfo.getArguments());
        }).findAny().orElse(null);
        if(resultMethod == null || resultMethod.getNextCalls() == null){
            return null;
        }

        if(set.contains(resultMethod)){//发现递归了
            return resultMethod.getNextCalls();
        }

        for (MethodInfo method : resultMethod.getNextCalls()) {
            set.add(method);
            method.setNextCalls(getMethod(method,fun,implFun, set));
            set.remove(method);
        }

        return resultMethod.getNextCalls();
    }
}

