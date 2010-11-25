//
// $Id$

package com.samskivert.jogviz.apt;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Name;

import com.samskivert.jogviz.Grapher;

/**
 * The main entry point for the visualizing processor.
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor
{
    @Override // from AbstractProcessor
    public void init (ProcessingEnvironment procenv)
    {
        super.init(procenv);

        if (!(procenv instanceof JavacProcessingEnvironment)) {
            procenv.getMessager().printMessage(
                Diagnostic.Kind.WARNING, "ASTVIZ requires javac v1.6.");
            return;
        }
        procenv.getMessager().printMessage(Diagnostic.Kind.NOTE, "ASTVIZ initialized.");

        _trees = Trees.instance(procenv);
        _rootmaker = TreeMaker.instance(((JavacProcessingEnvironment)procenv).getContext());
    }

    @Override // from AbstractProcessor
    public boolean process (Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (_trees == null) {
            return false;
        }

        for (Element elem : roundEnv.getRootElements()) {
            JCCompilationUnit unit = toUnit(elem);
            System.out.println("Root elem " + elem);
            Grapher.of(unit).
                literal(Name.class).
                literal(Type.class).
                literal(Symbol.ClassSymbol.class).
                filter(Scope.ImportScope.class).
                graph();
//             unit.accept(new GraphingVisitor());
        }
        return false;
    }

    protected JCCompilationUnit toUnit (Element element)
    {
        TreePath path = _trees.getPath(element);
        return (path == null) ? null : (JCCompilationUnit)path.getCompilationUnit();
    }

    protected static class GraphingVisitor extends TreeScanner
    {
        public void scan (JCTree tree)
        {
            System.out.println("Scanning " + what(tree));
            super.scan(tree);
        }

        public void visitTopLevel(JCCompilationUnit tree) {
            System.out.println(tree.sourcefile.getName());
            System.out.println("Package annotations");
            scan(tree.packageAnnotations);
            System.out.println("Package id");
            scan(tree.pid);
            System.out.println("Definitions");
            scan(tree.defs);
        }

//         public void visitImport(JCImport tree) {
//             scan(tree.qualid);
//         }

//         public void visitClassDef(JCClassDecl tree) {
//             scan(tree.mods);
//             scan(tree.typarams);
//             scan(tree.extending);
//             scan(tree.implementing);
//             scan(tree.defs);
//         }

//         public void visitMethodDef(JCMethodDecl tree) {
//             scan(tree.mods);
//             scan(tree.restype);
//             scan(tree.typarams);
//             scan(tree.params);
//             scan(tree.receiverAnnotations);
//             scan(tree.thrown);
//             scan(tree.defaultValue);
//             scan(tree.body);
//         }

//         public void visitVarDef(JCVariableDecl tree) {
//             scan(tree.mods);
//             scan(tree.vartype);
//             scan(tree.init);
//         }

//         public void visitSkip(JCSkip tree) {
//         }

//         public void visitBlock(JCBlock tree) {
//             scan(tree.stats);
//         }

//         public void visitDoLoop(JCDoWhileLoop tree) {
//             scan(tree.body);
//             scan(tree.cond);
//         }

//         public void visitWhileLoop(JCWhileLoop tree) {
//             scan(tree.cond);
//             scan(tree.body);
//         }

//         public void visitForLoop(JCForLoop tree) {
//             scan(tree.init);
//             scan(tree.cond);
//             scan(tree.step);
//             scan(tree.body);
//         }

//         public void visitForeachLoop(JCEnhancedForLoop tree) {
//             scan(tree.var);
//             scan(tree.expr);
//             scan(tree.body);
//         }

//         public void visitLabelled(JCLabeledStatement tree) {
//             scan(tree.body);
//         }

//         public void visitSwitch(JCSwitch tree) {
//             scan(tree.selector);
//             scan(tree.cases);
//         }

//         public void visitCase(JCCase tree) {
//             scan(tree.pat);
//             scan(tree.stats);
//         }

//         public void visitSynchronized(JCSynchronized tree) {
//             scan(tree.lock);
//             scan(tree.body);
//         }

//         public void visitTry(JCTry tree) {
//             scan(tree.body);
//             scan(tree.catchers);
//             scan(tree.finalizer);
//         }

//         public void visitCatch(JCCatch tree) {
//             scan(tree.param);
//             scan(tree.body);
//         }

//         public void visitConditional(JCConditional tree) {
//             scan(tree.cond);
//             scan(tree.truepart);
//             scan(tree.falsepart);
//         }

//         public void visitIf(JCIf tree) {
//             scan(tree.cond);
//             scan(tree.thenpart);
//             scan(tree.elsepart);
//         }

//         public void visitExec(JCExpressionStatement tree) {
//             scan(tree.expr);
//         }

//         public void visitBreak(JCBreak tree) {
//         }

//         public void visitContinue(JCContinue tree) {
//         }

//         public void visitReturn(JCReturn tree) {
//             scan(tree.expr);
//         }

//         public void visitThrow(JCThrow tree) {
//             scan(tree.expr);
//         }

//         public void visitAssert(JCAssert tree) {
//             scan(tree.cond);
//             scan(tree.detail);
//         }

//         public void visitApply(JCMethodInvocation tree) {
//             scan(tree.meth);
//             scan(tree.args);
//         }

//         public void visitNewClass(JCNewClass tree) {
//             scan(tree.encl);
//             scan(tree.clazz);
//             scan(tree.args);
//             scan(tree.def);
//         }

//         public void visitNewArray(JCNewArray tree) {
//             scan(tree.annotations);
//             scan(tree.elemtype);
//             scan(tree.dims);
//             for (List<JCTypeAnnotation> annos : tree.dimAnnotations)
//                 scan(annos);
//             scan(tree.elems);
//         }

//         public void visitParens(JCParens tree) {
//             scan(tree.expr);
//         }

//         public void visitAssign(JCAssign tree) {
//             scan(tree.lhs);
//             scan(tree.rhs);
//         }

//         public void visitAssignop(JCAssignOp tree) {
//             scan(tree.lhs);
//             scan(tree.rhs);
//         }

//         public void visitUnary(JCUnary tree) {
//             scan(tree.arg);
//         }

//         public void visitBinary(JCBinary tree) {
//             scan(tree.lhs);
//             scan(tree.rhs);
//         }

//         public void visitTypeCast(JCTypeCast tree) {
//             scan(tree.clazz);
//             scan(tree.expr);
//         }

//         public void visitTypeTest(JCInstanceOf tree) {
//             scan(tree.expr);
//             scan(tree.clazz);
//         }

//         public void visitIndexed(JCArrayAccess tree) {
//             scan(tree.indexed);
//             scan(tree.index);
//         }

//         public void visitSelect(JCFieldAccess tree) {
//             scan(tree.selected);
//         }

//         public void visitIdent(JCIdent tree) {
//         }

//         public void visitLiteral(JCLiteral tree) {
//         }

//         public void visitTypeIdent(JCPrimitiveTypeTree tree) {
//         }

//         public void visitTypeArray(JCArrayTypeTree tree) {
//             scan(tree.elemtype);
//         }

//         public void visitTypeApply(JCTypeApply tree) {
//             scan(tree.clazz);
//             scan(tree.arguments);
//         }

//         public void visitTypeParameter(JCTypeParameter tree) {
//             scan(tree.annotations);
//             scan(tree.bounds);
//         }

//         @Override
//         public void visitWildcard(JCWildcard tree) {
//             scan(tree.kind);
//             if (tree.inner != null)
//                 scan(tree.inner);
//         }

//         @Override
//         public void visitTypeBoundKind(TypeBoundKind that) {
//         }

//         public void visitModifiers(JCModifiers tree) {
//             scan(tree.annotations);
//         }

//         public void visitAnnotation(JCAnnotation tree) {
//             scan(tree.annotationType);
//             scan(tree.args);
//         }

//         public void visitAnnotatedType(JCAnnotatedType tree) {
//             scan(tree.annotations);
//             scan(tree.underlyingType);
//         }

//         public void visitErroneous(JCErroneous tree) {
//         }

//         public void visitLetExpr(LetExpr tree) {
//             scan(tree.defs);
//             scan(tree.expr);
//         }
    }

    protected static String what (JCTree node)
    {
        if (node == null) {
            return "null";
        } else {
            return node.getClass().getSimpleName() + "[" + node + "]";
        }
    }

    protected Trees _trees;
    protected TreeMaker _rootmaker;
}
