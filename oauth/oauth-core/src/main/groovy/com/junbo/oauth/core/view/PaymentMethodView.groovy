package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by Zhanxin on 5/21/2014.
 */
@CompileStatic
class PaymentMethodView extends AbstractView {
    private String btCSE
    private int parentControlAge

    @Required
    void setBtCSE(String btCSE) {
        this.btCSE = btCSE
    }

    @Required
    void setParentControlAge(int parentControlAge) {
        this.parentControlAge = parentControlAge
    }

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        int age = parentControlAge
        if (contextWrapper.dob != null) {
            age = this.calculateMyAge(contextWrapper.dob)
        }

        def modelMap = [
                'clientId'          : contextWrapper.client.clientId,
                'CES'               : btCSE,
                'needParentControl' : age < parentControlAge ? 'true' : 'false',
                'email'             : contextWrapper.userDefaultEmail,
                'locale'            : contextWrapper.viewLocale
        ]
        modelMap.putAll(contextWrapper.extraParameterMap)

        def model = new ViewModel(
                view: 'payment-method',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }

    private int calculateMyAge(Date dob) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dob);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Calendar birthCal = new GregorianCalendar(year, month, day)
        Calendar nowCal = new GregorianCalendar()

        int age = nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)

        boolean isMonthGreater = birthCal.get(Calendar.MONTH) >= nowCal.get(Calendar.MONTH)

        boolean isMonthSameButDayGreater = (birthCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)
            && birthCal.get(Calendar.DAY_OF_MONTH) > nowCal.get(Calendar.DAY_OF_MONTH))

        if (isMonthGreater || isMonthSameButDayGreater) {
            age = age - 1
        }

        return age
    }
}
