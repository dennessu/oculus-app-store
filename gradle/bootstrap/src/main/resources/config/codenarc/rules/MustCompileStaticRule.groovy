package config.codenarc.rules
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
/**
 * All groovy classes must @CompileStatic.
 *
 * @author Shu Zhang
 */
class MustCompileStaticRule extends AbstractAstVisitorRule {

    String name = 'MustCompileStaticRule'
    int priority = 2
    int compilerPhase = Phases.INSTRUCTION_SELECTION
    Class astVisitorClass = MustCompileStaticRuleAstVisitor
    String description = "All groovy classes must @CompileStatic. "
}

class MustCompileStaticRuleAstVisitor extends AbstractAstVisitor {

    void visitClassEx(ClassNode classNode) {
        if (!classNode.isInterface() &&
            !(classNode instanceof InnerClassNode) &&
            !classNode?.annotations?.any { it.classNode.name == "groovy.transform.CompileStatic" }) {
            addViolation(classNode, "The class $classNode.name didn't specify @CompileStatic")
        }
        super.visitClassEx(classNode)
    }
}
