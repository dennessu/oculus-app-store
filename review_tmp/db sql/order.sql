                create table user_order (
                    order_id bigint not null,
                    user_id bigint not null,
                    original_order_id bigint default null,
                    order_type_id smallint not null,
                    requestor_id varchar(128) not null,
                    onbehalfof_requestor_id varchar(128) default null,
                    tracking_uuid uuid not null,
                    country_id smallint not null,
                    currency_id smallint not null,
                    shipping_method_id smallint default null,
                    shipping_address_id bigint default null,
                    properties varchar(4000) default null,
                    created_date timestamp not null,
                    created_by varchar(128) not null,
                    modified_date timestamp not null,
                    modified_by varchar(128) not null,
                    primary key (order_id)
                );
                create unique index user_order_tracking_uuid_index on user_order (user_id, order_id, tracking_uuid);
                create unique index user_order_date_index on user_order (user_id, order_id, modified_date, created_date);

                create table order_event (
                    event_id bigint not null,
                    action_id smallint not null,
                    status_id smallint not null,
                    event_date timestamp not null,
                    order_id bigint not null,
                    primary key (event_id)
                );
                create unique index order_event_id_event_date_index on order_event (event_id, event_date);

                create table order_item (
                    order_item_id bigint not null,
                    order_id bigint not null,
                    order_item_type smallint not null,
                    product_item_id varchar(128) not null,
                    product_sku_id varchar(128) not null,
                    product_item_version varchar(128) not null,
                    unit_price decimal not null,
                    quantity int not null,
                    total_price decimal not null,
                    description varchar(256) default null,
                    federated_id varchar(128) default null,
                    properties varchar(4000) default null,
                    created_date timestamp not null,
                    created_by varchar(128) not null,
                    modified_date timestamp not null,
                    modified_by varchar(128) not null,
                    primary key (order_item_id)
                );
                create unique index item_order_date_index on order_item (order_item_id, order_id, modified_date, created_date);

                create table order_discount_info (
                    order_item_discount_info_id bigint not null,
                    order_id bigint not null,
                    order_item_id bigint default null,
                    discount_type smallint not null,
                    discount_rate decimal default null,
                    discount_amount decimal not null,
                    created_date timestamp not null,
                    created_by varchar(128) not null,
                    modified_date timestamp not null,
                    modified_by varchar(128) not null,
                    primary key (order_item_discount_info_id)
                );
                create unique index discount_order_index on order_discount_info (order_item_discount_info_id, order_id);

                create table order_billing_event (
                    event_id bigint not null,
                    action_id smallint not null,
                    status_id smallint not null,
                    event_date timestamp not null,
                    order_id bigint not null,
                    balance_id varchar(128) not null,
                    primary key (event_id)
                );
                create unique index event_order_balance_date_index on order_billing_event (event_id, order_id, balance_id, event_date);

                create table order_item_fulfillment_event (
                    event_id bigint not null,
                    action_id smallint not null,
                    status_id smallint not null,
                    event_date timestamp not null,
                    order_id bigint not null,
                    order_item_id bigint not null,
                    tracking_uuid uuid not null,
                    fulfillment_id varchar(128) not null,
                    primary key (event_id)
                );
                create unique index fulfillment_event_order_item_date_index on order_item_fulfillment_event (event_id, order_id, order_item_id, event_date);
                create unique index fulfillment_event_tracking_uuid_index on order_item_fulfillment_event (event_id, tracking_uuid);

                create table order_item_preorder_info (
                    order_item_preorder_info_id bigint not null,
                    order_item_id bigint not null,
                    billing_date timestamp not null,
                    pre_notification_date timestamp default null,
                    release_date timestamp not null,
                    created_date timestamp not null,
                    created_by varchar(128) not null,
                    modified_date timestamp not null,
                    modified_by varchar(128) not null,
                    primary key (order_item_preorder_info_id)
                );
                create unique index preorder_info_order_item_date_index on order_item_preorder_info (order_item_preorder_info_id, order_item_id, billing_date, pre_notification_date, release_date);
                create unique index preorder_info_date_index on order_item_preorder_info (order_item_preorder_info_id, created_date, modified_date);

                create table order_item_preorder_event (
                    event_id bigint not null,
                    action_id smallint not null,
                    status_id smallint not null,
                    event_date timestamp not null,
                    order_item_id bigint not null,
                    primary key (event_id)
                );
                create unique index event_order_item_date_index on order_item_preorder_event (event_id, order_item_id, event_date);

                create table order_item_preorder_update_history (
                    order_item_preorder_update_history_id bigint not null,
                    order_item_id bigint not null,
                    update_type_id smallint not null,
                    update_column varchar(128) not null,
                    update_before_value varchar(128) not null,
                    update_after_value varchar(128) not null,
                    updated_date timestamp not null,
                    updated_by varchar(128) not null,
                    primary key (order_item_preorder_update_history_id)
                );
                create unique index history_order_item_date_index on order_item_preorder_update_history (order_item_preorder_update_history_id, order_item_id, updated_date);

                create table order_item_tax_info (
                    order_item_tax_info_id bigint not null,
                    order_item_id bigint not null,
                    total_tax decimal not null,
                    tax_code varchar(128) not null,
                    is_tax_inclusive boolean not null,
                    is_tax_exempt boolean not null,
                    created_date timestamp not null,
                    created_by varchar(128) not null,
                    modified_date timestamp not null,
                    modified_by varchar(128) not null,
                    primary key (order_item_tax_info_id)
                );
                create unique index tax_item_date_index on order_item_tax_info (order_item_tax_info_id, order_item_tax_info_id, created_date, modified_date);

                create table order_payment_info (
                    order_payment_info_id bigint not null,
                    order_id bigint not null,
                    payment_method_id varchar(128) not null,
                    payment_method_type varchar(128) not null,
                    created_date timestamp not null,
                    created_by varchar(128) not null,
                    modified_date timestamp not null,
                    modified_by varchar(128) not null,
                    primary key (order_payment_info_id)
                );
                create unique index payment_order_index on order_payment_info (order_payment_info_id, order_payment_info_id, payment_method_id);
                create unique index payment_date_index on order_payment_info (order_payment_info_id, created_date, modified_date);

                create table subledger (
                    subledger_id bigint not null,
                    seller_id bigint not null,
                    seller_tax_profile_id bigint default null,
                    currency_id smallint not null,
                    total_amount decimal not null,
                    property varchar(4000) default null,
                    created_date timestamp not null,
                    created_by varchar(128) not null,
                    modified_date timestamp not null,
                    modified_by varchar(128) not null,
                    primary key (subledger_id)
                );

                create table subledger_event (
                    event_id bigint not null,
                    action_id smallint not null,
                    status_id smallint not null,
                    event_date timestamp not null,
                    subledger_id bigint not null,
                    primary key (event_id)
                );

                create table subledger_item (
                    subledger_item_id bigint not null,
                    subledger_id bigint not null,
                    order_item_id bigint not null,
                    original_subledger_item_id bigint default null,
                    total_amount decimal not null,
                    created_date timestamp not null,
                    created_by varchar(128) not null,
                    modified_date timestamp not null,
                    modified_by varchar(128) not null,
                    primary key (subledger_item_id)
                );

                create table subledger_item_event (
                    event_id bigint not null,
                    action_id smallint not null,
                    status_id smallint not null,
                    event_date timestamp not null,
                    subledger_item_id bigint not null,
                    primary key (event_id)
                );