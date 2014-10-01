package com.junbo.langur.core.webflow.state

/**
 * Created by kg on 2/26/14.
 */
interface StateRepository {

    Conversation newConversation()

    Conversation loadConversation(String conversationId)

    void persistConversation(Conversation conversation) // delete from the persistence if flowStack is empty
}
