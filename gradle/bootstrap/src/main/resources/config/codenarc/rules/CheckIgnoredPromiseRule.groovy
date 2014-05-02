package config.codenarc.rules
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
/**
 * In async concurrent programming, the returning Promise should never be ignored.
 *
 * @author Shu Zhang
 */
class CheckIgnoredPromiseRuleRule extends AbstractAstVisitorRule {

    String name = 'CheckIgnoredPromiseRule'
    int priority = 2
    int compilerPhase = Phases.INSTRUCTION_SELECTION
    Class astVisitorClass = CheckIgnoredPromiseRuleAstVisitor
    String description =
            "Ignoring Promise<> or ListenableFuture<> will lead to unexpected behavior. " +
            "It usually means some asynchronous action is issued but not checked again later." +
            "If it meant to be the return value, use return statement explicitly to suppress this rule."
}

class CheckIgnoredPromiseRuleAstVisitor extends AbstractAstVisitor {
    void visitExpressionStatement(ExpressionStatement statement) {
        if (isFirstVisit(statement) && statement.expression instanceof MethodCallExpression) {
            if (statement.expression.type.name == 'com.junbo.langur.core.promise.Promise') {
                addViolation(statement, 'The Promise is ignored. If it is a return value, please explicitly add return.')
            } else if (statement.expression.type.name == 'com.google.common.util.concurrent.ListenableFuture') {
                addViolation(statement, 'The ListenableFuture is ignored. If it is a return value, please explicitly add return.')
            }
        }
        super.visitExpressionStatement(statement)
    }
}
