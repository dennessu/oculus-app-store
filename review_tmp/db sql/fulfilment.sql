            create table fulfiment_request (
                request_id            bigint          not null,
                tracking_guid         uuid            not null,
                payload               json            not null,
                billing_order_id      bigint          default null,
                created_by            varchar(64)     not null,
                created_date          timestamp       not null,
                updated_by            varchar(64)     default null,
                updated_date          timestamp       default null,

                primary key (request_id)
            );
			
			create table fulfiment (
                fulfilment_id         bigint          not null,
                request_id            bigint          not null,
                type                  int             not null,
                payload               json            not null,
                status                int             not null,
                result                json                null,
                created_by            varchar(64)     not null,
                created_date          timestamp       not null,
                updated_by            varchar(64)     default null,
                updated_date          timestamp       default null,

                primary key (fulfilment_id)
            );