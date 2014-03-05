create table cart_product_item (
  product_item_id bigint not null,
  cart_id bigint not null,
  item_id bigint not null,
  sku_id bigint not null,
  bundle_id	varchar(200) default null,
  bundle_type_id smallint not null,
  quantity bigint not null,
  status_id smallint not null,
  selected boolean not null,
  properties varchar(4000) default null,
  date_created timestamp not null,
  date_modified timestamp not null,
  primary key (product_item_id)
);

create table cart_coupon_item (
  coupon_item_id bigint not null,
  cart_id bigint not null,
  coupon_code varchar(256) not null,
  status_id smallint not null,
  properties varchar(4000) default null,
  date_created timestamp not null,
  date_modified timestamp not null,
  primary key (coupon_item_id)
);