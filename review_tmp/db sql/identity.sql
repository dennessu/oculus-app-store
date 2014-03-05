            create table user_account (
              user_id bigint not null,
              user_name varchar(256) not null,
              type smallint not null,
              country_locale varchar(5) not null,
              language_locale varchar(5) not null,
              dob date null,
              timezone varchar(10),
              default_billing_account_id bigint,
              default_shipping_address_id bigint,
              status smallint not null,
              status_reason_code smallint,

              created_by varchar(32) not null,
              created_time timestamp not null,
              modified_by varchar(32),
              modified_time timestamp,

              primary key (user_id)
            );

            create table user_type (
              id int not null,
              name varchar(64) not null,

              created_by varchar(32) not null,
              created_time timestamp not null,
              primary key (id)
            );

            create table user_status (
              id int not null,
              name varchar(64) not null,

              created_by varchar(32) not null,
              created_time timestamp not null,
              primary key (id)
            );

            insert into user_status values( 1 , 'volatile' , '' ,'2013-01-23');
            insert into user_status values( 2 , 'pending' , '' ,'2013-01-23');
            insert into user_status values( 3 , 'active' , '' ,'2013-01-23');
            insert into user_status values( 4 , 'deactivated' , '' ,'2013-01-23');
            insert into user_status values( 5 , 'disabled' , '' ,'2013-01-23');
            insert into user_status values( 6 , 'tentative' , '' ,'2013-01-23');
            insert into user_status values( 7 , 'deleted' , '' ,'2013-01-23');
            insert into user_status values( 8 , 'banned' , '' ,'2013-01-23');
            insert into user_status values( 9 , 'child_pending' , '' ,'2013-01-23');
            insert into user_status values( 10 , 'child_approved' , '' ,'2013-01-23');

            create table user_status_reason_code(
              id int not null,
              name varchar(64) not null,

              created_by varchar(32) not null,
              created_time timestamp not null,
              primary key (id)
            );

            insert into user_status_reason_code values (  1, 'none',         '', '2013-01-23');
            insert into user_status_reason_code values (  2, 'reactivated_customer',       '', '2013-01-23');
            insert into user_status_reason_code values (  3, 'invalid_email',        '', '2013-01-23');
            insert into user_status_reason_code values (  4, 'privacy_policy',        '', '2013-01-23');
            insert into user_status_reason_code values (  5, 'parents_request',         '', '2013-01-23');
            insert into user_status_reason_code values (  6, 'suspended_misconduct_general',         '', '2013-01-23');
            insert into user_status_reason_code values (  7, 'suspended_misconduct_harassment',         '', '2013-01-23');
            insert into user_status_reason_code values (  8, 'suspended_misconduct_macroing',         '', '2013-01-23');
            insert into user_status_reason_code values (  9, 'suspended_misconduct_exploitation',         '', '2013-01-23');
            insert into user_status_reason_code values (  10, 'suspended_fraud',         '', '2013-01-23');
            insert into user_status_reason_code values (  11, 'customer_opt_out',         '', '2013-01-23');
            insert into user_status_reason_code values (  12, 'customer_under_age',         '', '2013-01-23');
            insert into user_status_reason_code values (  13, 'email_confirmation_required',         '', '2013-01-23');
            insert into user_status_reason_code values (  14, 'mistyped_id',         '', '2013-01-23');
            insert into user_status_reason_code values (  15, 'abused_id',         '', '2013-01-23');
            insert into user_status_reason_code values (  16, 'deactivated_email_link',         '', '2013-01-23');
            insert into user_status_reason_code values (  17, 'deactivated_cs',         '', '2013-01-23');
            insert into user_status_reason_code values (  18, 'claimed_by_true_owner',         '', '2013-01-23');
            insert into user_status_reason_code values (  19, 'banned',         '', '2013-01-23');
            insert into user_status_reason_code values (  20, 'banned_chargeback_received',         '', '2013-01-23');
            insert into user_status_reason_code values (  21, 'permanent_ban_rso',         '', '2012-08-20');

            create table user_profile (
              id bigint not null,
              user_id bigint not null,
              type smallint not null,

              first_name varchar(256),
              last_name varchar(256),
              company_name varchar(512),
              role_type int,
              payout_currency varchar(3),

              created_by varchar(32) not null,
              created_time timestamp not null,
              modified_by varchar(32),
              modified_time timestamp,

              primary key (id)
            );

            create table email (
              id bigint not null,
              user_id bigint not null,
              type smallint,
              value varchar(256),
              verified smallint,
              verified_time timestamp,

              created_by varchar(32) not null,
              created_time timestamp not null,
              modified_by varchar(32),
              modified_time timestamp,

              primary key(id)
            );

            create table phone_number (
              id bigint not null,
              user_id bigint not null,
              type smallint null,
              value varchar(256) not null,
              verified smallint,
              verified_time timestamp,

              created_by varchar(32) not null,
              created_time timestamp not null,
              modified_by varchar(32),
              modified_time timestamp,

              primary key(id)
            );

            create table sec_ques (
              id bigint not null,
              user_id bigint not null,
              sec_question varchar(2000),
              sec_answer varchar(2000),

              created_by varchar(32) not null,
              created_time timestamp not null,
              modified_by varchar(32),
              modified_time timestamp,

              primary key(id)
            );

            create table user_tos (
              id bigint not null,
              user_id bigint not null,
              tos_accepted varchar(2000) not null,
              date_accepted timestamp not null,
              status smallint not null default 1,

              created_by varchar(32) not null,
              created_time timestamp not null,
              modified_by varchar(32),
              modified_time timestamp,

              primary key (id)
            );

            create table user_optins (
              id bigint not null,
              user_id bigint not null,
              user_optins_type smallint not null ,
              value smallint not null ,

              created_by varchar(32) not null,
              created_time timestamp not null,
              modified_by varchar(32),
              modified_time timestamp,

              primary key (id)
            );

            create table user_optins_type(
              id smallint not null,
              name varchar(64) not null,
              description varchar(128) not null,
              created_by varchar(32) not null,
              created_time timestamp not null,
              primary key (id)
            );

            create table user_device_profile (
              id bigint not null,
              user_id bigint not null,
              platform_type varchar(20) not null,
              type varchar(10) not null ,
              value varchar(256) not null ,

              created_by varchar(32) not null,
              created_time timestamp not null

              primary key (id)
            );

            create table user_password (
              user_id bigint not null,
              password_hash varchar(128) not null,
              password_hash_salt varchar(60) null,
              temp_password varchar(128) null,
              temp_password_expiry_time timestamp null,
              password_strength smallint,

              created_by varchar(32) not null,
              created_time timestamp not null,
              modified_by varchar(32),
              modified_time timestamp,

              primary key (user_id)
            );

            create table password_strength(
              id smallint not null,
              name varchar(64) not null,

              created_by varchar(32) not null,
              created_time timestamp not null,

              primary key (id)
            );

            insert into password_strength values( 1 , 'weak' , '' , '2013-01-23' );
            insert into password_strength values( 2 , 'strong' , '' , '2013-01-23' );

            create table authentication_event(
              id bigint not null,
              user_id bigint not null,

              authentication_source varchar(64),
              authorization_time timestamp,
              authorization_ip_address varchar(15),
              authorization_device varchar(64),
              primary key(id)
            );

            create table registration_event(
              id bigint not null,
              user_id bigint not null,

              registration_source varchar(64),
              registration_time timestamp,
              registration_ip_address varchar(15),
              registration_device varchar(64),
              primary key(id)
            );

            create table federation_reference(
                id bigint not null,
                user_id bigint not null,
                type smallint,
                value varchar(512),

                created_by varchar(32) not null,
                created_time timestamp not null,
                primary key(id)
            );

            create table federation_reference_type(
                id smallint not null,
                name varchar(64) not null,

                created_by varchar(32) not null,
                created_time timestamp not null,
                primary key (id)
            );
			
            create table app(
                id bigint not null,
                owner_id bigint not null,
                name varchar(255) not null,
                
                redirect_uris TEXT,
                default_redirect_uri TEXT,
                logout_uris TEXT,
                
                response_types TEXT,
                grant_types TEXT,
                ip_whitelist TEXT,
                properties TEXT,
                
                primary key(id)
                );
                
            create table app_secrect(
                id bigint not null,
                app_id bigint not null,
                
                value varchar(255),
                expires_by timestamp,
                status varchar(255),
                
                primary key(id)
                );    
            create table app_group(
                id bigint not null,
                app_id bigint not null,
                
                permissions varchar(255) not null,
                
                primary key(id)
            );
            
            create table app_group_assoc(
                id bigint not null,
                app_group_id bigint not null,
                user_id bigint not null,
                
                primary key(id)
            );	

            create table authorization_code(
                id bigint not null,
                
                code_value varchar(255) not null,
                app_id bigint not null,
                scopes TEXT not null,
                user_id bigint not null,
                expires_by timestamp,
                
                nonece TEXT,
                redirect_uri TEXT,
                properties TEXT,
                
                primary key(id)
            );
            
            create table access_token(
                id bigint not null,
                
                token_value varchar(255) not null,
                app_id bigint not null,
                
                scopes TEXT not null,
                user_id bigint not null,
                expires_by timestamp,
                
                properties TEXT,
                
                primary key(id)
            );
            
            create table refresh_token(
                id bigint not null,
                
                token_value varchar(255) not null,
                app_id bigint not null,
                scopes TEXT not null,
                user_id bigint not null,
                expires_by timestamp,
                
                salt varchar(255),
                is_stolen boolean,
                properties TEXT,
                
                primary key(id)
            );			