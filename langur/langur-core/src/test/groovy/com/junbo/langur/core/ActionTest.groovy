package com.junbo.langur.core
import com.junbo.langur.core.action.*
import com.junbo.langur.core.promise.ExecutorContext
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.testng.Assert
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit
/**
 * Created by Shenhua on 1/6/14.
 */
@CompileStatic
class ActionTest {

    private final ActionExecutor executor = new DefaultActionExecutor()
    private boolean asyncModeOldValue;

    @BeforeTest
    void setup() {
        asyncModeOldValue = ExecutorContext.isAsyncMode();
        ExecutorContext.setAsyncMode(true);
    }

    @AfterTest
    void cleanup() {
        ExecutorContext.setAsyncMode(asyncModeOldValue);
    }

    @Test
    void testSequenceAction() {

        int value = 0

        def seq1 = new SequenceAction(
                [new Action() {
                    @Override
                    Promise<ActionResult> execute(ActionContext context) {
                        Assert.assertEquals(context.currentAction, this)
                        value++
                        Promise.pure(ActionResult.NEXT)
                    }
                }
                        , new Action() {
                    @Override
                    Promise<ActionResult> execute(ActionContext context) {
                        Assert.assertEquals(context.currentAction, this)
                        value++
                        Promise.pure(ActionResult.NEXT)
                    }
                }
                        , new NamedAction() {
                    final String name = 'LOOP'

                    @Override
                    Promise<ActionResult> execute(ActionContext context) {
                        Assert.assertEquals(context.currentAction, this)
                        value++
                        Promise.pure(ActionResult.NEXT)
                    }
                }
                        , new Action() {
                    @Override
                    Promise<ActionResult> execute(ActionContext context) {
                        Assert.assertEquals(context.currentAction, this)
                        value++
                        Promise.pure(ActionResult.nextAction('LOOP'))
                    }
                }
                ] as List<Action>,
                executor
        )

        Promise<ActionResult> result = executor.execute(seq1, new ActionContext())

        try {
            result.get()
            Assert.fail('test failure')
        }
        catch (Exception ex) {
            assert (ex.cause)
        }
    }

    @Test
    void testBranchAction() {

        int value = 0

        def branch1 = new SwitchAction(
                new Action() {
                    @Override
                    Promise<ActionResult> execute(ActionContext context) {
                        Promise.pure(ActionResult.nextAction('branch1'))
                    }
                }
                , [new NamedAction() {
            final String name = 'branch1'

            @Override
            Promise<ActionResult> execute(ActionContext context) {
                Assert.assertEquals(context.currentAction, this)
                value++
                Promise.pure(ActionResult.NEXT)
            }
        }
                , new NamedAction() {
            final String name = 'branch2'

            @Override
            Promise<ActionResult> execute(ActionContext context) {
                Assert.assertEquals(context.currentAction, this)
                value++
                return Promise.pure(ActionResult.NEXT)
            }
        }
        ], new Action() {
            @Override
            Promise<ActionResult> execute(ActionContext context) {
                return Promise.pure(ActionResult.NEXT)
            }
        }
                , executor
        )

        Promise<ActionResult> result = executor.execute(branch1, new ActionContext())
        result.get()
        assert (result)
    }

    @Test
    void testPromiseContext() {

        ThreadLocal<String> context = new ThreadLocal<String>();

        context.set('value1')

        Promise<String> promise = Promise.delayed(1, TimeUnit.SECONDS) {
            assert 'value1' == context.get()
            null
        }.then { String s ->
            return Promise.pure(s)
        }

        promise.get()
        assert (promise)
    }
}
