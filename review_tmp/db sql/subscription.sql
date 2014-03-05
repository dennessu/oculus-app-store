            create table subscription (
              subscription_id bigint not null,
              user_id bigint not null,
              pi_id bigint,
              state_id smallint not null,
              item_id bigint not null,
              revision bigint not null,
              subs_start_date timestamp,
              subs_end_date timestamp,
              group_id bigint,
              source varchar(256),
              inserted_time timestamp,
              updated_time timestamp,
              primary key (subscription_id)
            );

            create table subscription_event (
              subscription_id bigint not null,
              subs_event_id int not null,
              event_type_id smallint not null,
              event_status_id smallint not null,
              retry_count int,
              inserted_time timestamp,
              updated_time timestamp,
              primary key (subscription_id, subs_event_id)
            );

            create table subs_event_status ( (
              event_status_id smallint not null,
              status_description varchar(64) not null,
              inserted_by varchar(32),
              inserted_time timestamp,
              primary key (event_status_id)
            );

            create table subscription_event_action (
              subscription_id bigint not null,
              subs_event_id int not null,
              subs_action_id int not null,
              action_type_id smallint not null,
              action_status_id smallint not null,
              retry_count int,
              request text,
              response text,
              inserted_time timestamp,
              updated_time timestamp,
              primary key (subscription_id, subscription_event_id)
            );

            create table subs_action_status ( (
              subs_action_id smallint not null,
              status_description varchar(64) not null,
              inserted_by varchar(32),
              inserted_time timestamp,
              primary key (subs_action_id)
            );

            create table subscription_entitlement ( (
              subscription_id bigint not null,
              entitlement_id bigint not null,
              entitlement_status varchar(32),
              inserted_time timestamp,
              updated_time timestamp,
              primary key (subscription_id, entitlement_id)
            );

            create table subscription_invoice ( (
              subscription_id bigint not null,
              invoice_id bigint not null,
              invoice_item_id bigint not null,
              invoice_status varchar(32),
              inserted_time timestamp,
              updated_time timestamp,
              primary key (subscription_id, invoice_id, invoice_item_id)
            ); 