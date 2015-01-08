This document summarize cache design discussed with Marshall.

## Overview
There are two layers of caches in the system:
  * Raw Cache. The raw cache contains the docs stored in cloudant DB. When reading from cloudant, the API will try to get the docs from the raw cache before actually calling to cloudant.
  * Cooked Cache. The cooked cache contains the resource full representation. An API can be responded immediately if it can be found in cooked cache.

Both caches need to be invalidated when the resource changes. This is done by 3 mechanisms:
  1. When doing the write operations, invalidate the cache.
  1. In the JVM backend, in each region there is Cloudant Sniffer. Once the cloudant doc is changed the sniffer will be notified by cloudant. Then the sniffer can invalidate the cache.
  1. As a safe guard, the cached item has a limited expiration time.

If all changes made are in the same region, the first mechanism will be good enough. The Cloudant Sniffer is used to catch changes happened in another region.

There are a lot of details on how this is implemented shown in the rest of the document.

## Cache Operation Rules

The cache is updated by multiple threads on multiple app servers. We hope the cloudant operations or API calls with cache can meet the following requirements:

1. If there is a read after a write to the same cloudant doc or resource from the same thread, the value read should contain the changes made by the write. The read result is from the cache in most cases.
1. If there are multiple reads to the same cloudant doc or resource, the later reads are from the cache in most cases.
1. If there are multiple writes from different threads at the same time, the cache will contain the latest write or be invalidated. It should not contain a dirty value.
1. If there is a write in another region, the cache in current region will be invalidated shortly after the cloudant replicates the change.

Note that if one thread is writing a doc, it's okay if another thread gets the old doc before the writing thread finishes updates the cache. The read from the other thread is not dependent on the write, so we can just imagine the read is done before the write. The application logic won't be broken by the order change. If there is a dependency, the read should be scheduled after the write finishes updating the cache.

To make sure the cache update don't conflict with write operations in other thread/server, the update will use the CAS feature of memcache. The cache operations can be implemented by:
  * Add to cache
    1. Use `add()` in the memcache client to add to cache.
    1. When `add()` fails, it means the item with the same key already exists in cache. This is a write conflict, invalidate the cache using `delete()` to avoid dirty data.
  * Update cache
    1. Get the cloudant doc or resource from the cache with CAS using `gets()`
    1. If the item does not exists, add to cache
    1. If the item exists and the revision equals to the cloudant doc or resource before the update, use `cas()` to update the resource.
      * If the `cas()` fails, it means there is other updates to the cache between `gets()` and `cas()`. Invalidate the cache using `delete()` to avoid dirty data.
      * There is an optional optimization for this scenario. The cloudant sniffer often invalidates the cache between `gets()` and `cas()` on the app server. This happens frequently during local cache test. This can be improved by an additional `add()` when the `cas()` fails.
        * If the `add()` failed, it means there is a conflict and we need to invalidate the cache.
        * If the `add()` succeeded, it means the cached item was deleted somehow. It's most likely to be deleted by sniffer. And the call to `add()` restores the updated value to the cache.
        * However it's also possible that it is deleted for other reason, like update conflicts detected or a delete operation. So this improvement can introduce dirty data in small possibility.
    1. If the item exists and the revision equals the cloudant doc or resource after the update, it's rare but some other thread may get and put the entity to cache already. Leave the cache as it is.
    1. Otherwise, it means the entity exists and the revision is different from the cloudant or doc before and after the update. This indicates a conflict in cache update. In this case the code should invalidate the cache using `delete()`.

#### Handling Operation Errors

Sometimes the cache operation can timeout. Now the timeout of memcache set in JVM is 100ms.

When the timeout occurs, the code will try to invalidate the cache of the entity it tries to get/add/update using `delete()`. If the `delete()` fails again, the error will be traced. This will leave dirty data in the cache.

The errors when calling cache will not lead to 500 error in the API response. However they will be shown in the logs.

If the cloudant call or API call fails for any reason, the cached item is also invalidated. The cloudant doc or resource may already be changed before the API fails, so the cached resource may already be invalid.

#### Cache Expiration

To limit the impact to bugs in the cache operations, a limited expiration can be set to the cache. In JVM the cache expiration is 1 hour.

## Raw Cache

The raw cache contains the docs stored in cloudant DB.

### Cache Implementation

The cloudant operations are implemented as below:

  * Cloudant Get
    1. Try to get from the raw cache
    1. If found in raw cache, return the cached value
    1. Try to get from cloudant
    1. If found, add the value to cache
    1. Return the value found
  * Cloudant Post
    1. Post to cloudant
    1. Invalidate the cache and return if post fails
    1. Add the new value to raw cache
  * Cloudant Put
    1. Put to cloudant
    1. Invalidate the cache and return if put fails
    1. Update the cache with new value in response
  * Cloudant Delete
    1. Delete from cloudant
    1. Invalidate the cache no matter whether the delete fails or not
      * It is possible some conflicting get can leave dirty data in the cache. It happens when the cloudant get received the response and then a delete happens and invalidates the cache. After that, the get puts the response to the cache. The chance is very small.
  * Cloudant Get Many
    1. Get from cloudant view or `_all_docs` for doc IDs
    1. For each doc ID, get the doc by ID.
      * The get will try to find the doc from raw cache first.

The implementations use the cache operations defined in cache operation rules.

### Special Handling

In the access logic of `encrypt_personal_info`, the code will try to get from local cloudant first. If the doc is not found, the code will try to get the doc from the home region of the doc ID. If the doc is found in the cloudant from another region, the result is not cached.

The reason is that the DB `encrypt_personal_info` is not replicated in all regions. If the update happens in the home region of the ID and not replicated, the cache will not be invalidated by the sniffer and the dirty data will stay until expired.

### Cache Key

Since the `encrypt_user_personal_info` and `hash_user_personal_info` both use the same ID, the raw cache encodes the cloudant DB name into the cache key. The raw cache key is:

`id + ":" + dbUri.dbName`

[**Note**] I looked at the code, the key used in JVM is now:

`id + ":" + dbUri.fullDbName + ":" + dbUri.cloudantUri.dc`

The DC is unnecessary because we don't cache results from other DCs. So it must be current DC. The `fullDbName` is the DB name with prefix. For example, in staging the user DB is `stg_user`. It was for shard accounts. It's also unnecessary. I'll make the change. The deployment of this change will invalidate all cached items. We can do it when the traffic is low.

## Cooked Cache

The cooked cache contains the resources responded in the GET/PUT/POST APIs. Marshall want it implemented in sewer. The sewer should cache the resource before expanding it.

### Cache Implementation

The API calls with cache can be implemented as below:

  * GET API (single resource)
    1. Try to get from the cooked cache
    1. If found in cooked cache, return the cached value
    1. Try to call the underlying API to get the response
    1. If API is successful, add the response to cache
      * Unless the API response header contains `oculus-cachable: false`
    1. Return the response
  * POST API
    1. Call the underlying API to get the response
    1. Invalidate the cache and return if the underlying API fails
    1. Add the response to cooked cache
  * PUT API
    1. Call the underlying API to get the response
    1. Invalidate the cache and return if the underlying API fails
    1. Update the cache with new value in response
  * DELETE API
    1. Call the underlying API
    1. Invalidate the cache no matter whether the underlying API fails or not
      * It is possible some conflicting get can leave dirty data in the cache. It happens when the GET API received the response and then a delete happens and invalidates the cache. After that, the get puts the response to the cache. The chance is very small.
  * GET collection API
    1. Call the underlying API and return the response
      * We don't have a good solution to utilize cache in this case.

The implementations use the cache operations defined in cache operation rules.

The operations above are only valid for resource APIs. APIs like oauth cannot be cached.

### Special Handling

If the underlying API responses with output header `oculus-cachable: false`, the result should not be cached and existing cached values should be invalidated.

This header is output for resources which came from other region and the resource may not be replicated to current region. The only example we know is the user-personal-info resource. In current setup, the Europe user-personal-info is not replicated to US. So sniffer in US will not know if the resource is updated in Europe.

[**Open Question**] For SQL first resources, the writes will still be replicated by cloudant due to dual-write. In the email discussion with Marshall, we wished to invalidate these resources before the call is routed. However since SQL resources are always routed to their home shard for updates, and GET from local cloudant unless `no-cache` header is added, it seems unnecessary to invalidate the raw cache.

If it's still needed to invalidate the raw cache before the calls to these APIs, the sewer needs a map for the raw cache DB names for these resources. This can be done in JVM because JVM does the home region routing.

[**Open Question**] When the `Cache-Control: no-cache` input header is found, the get from cache logic may need to be skipped. However the cache update and invalidation should still happen.

### Cache Key

The cooked cache uses only the ID of the resource with a constant prefix as the cache key. The cache key is:

`"<COOKED>" + resourceId`

To prevent the risk that the resource ID duplicates and lead to wrong result from the cache, the cache value should contain an extra json field called `__RESOURCE_TYPE__`.

When getting from the cache, the code will verify the `__RESOURCE_TYPE__` matches the input resource type. If it doesn't match, the code will pretend the cooked cache didn't match.

This makes the cache simpler and makes the sniffer easier to invalidate the cooked cache. The sniffer don't need to maintain a map from cloudant DB names to resource types.

## Cloudant Sniffer

For each region\*env, there should be N cache-mgmt servers. Each of these cache-mgmt servers opens a "changes feed" from each relevant Cloudant DB. That means 110\*N changes-feeds in Prod, 110\*N in Preprod, 110\*N in Staging, and 110\*N in Integ. From Cloudant’s perspective, the physical cluster handling Prod & Preprod is the same so Cloudant will see 2\*110\*N incoming "changes feeds" on the Prod/Preprod cluster; and similarly in US West Staging & Integ are the same so that region’s ‘secondary’ Cloudant cluster will see 2\*110\*N "changes feeds". Each "changes feed" should return ONLY the ID and REV of the affected resource – it should not return the entire item representation.

The N servers do exactly the same thing. So if one of them is shutdown, the others will still do the work. When deploying the upgrade, the N servers can go half-half, so the sniffer won't stop during the deployment.

[**Open Question**]
1. Are the cache-mgmt servers designated? The load is quite low on the sniffer today.
1. I'd recommend N = 2 to save the server count.

### Sniffer Implementation

The sniffer will do the following:

  1. Find all cloudant DBs we have according to the configuration.
  1. For each DB, fork a thread and read the `_change` feed using continuous mode.
  1. If there is a change feed, and the revision is not the same as the item found in memcached, invalidate the cached item from memcached.

[** Open Question**]
To invalidate the casey resources in cooked cache, the casey cloudant account and api key need to be added to the configuration.

### Sniffer Monitoring

Now there is a health check which run every 5 minutes to verify that the sniffer is still working.

  * Insert a new doc in ping cloudant DB
  * Delete the doc from cloudant without touching the cache
  * Repeat the read until the doc is unavailable from the API
  * If the doc is still available from the API after 5 seconds, the health check is considered fail

Everyday there are a few failures, so the alerting rule is tuned to ignore the noises.

After cooked cache is implemented, the health check should be extended. [**TBD**]

## Cache Setup

We use memcached as the cache storage. The memcache clients are available in major programming languages.
  * Java: [net.spy.memcached Client](https://code.google.com/p/spymemcached/)
  * Node.js: [memcached package](https://www.npmjs.com/package/memcached)

As discussed, the raw cache and cloudant sniffer are implemented in JVM, and the cooked cache is implemented in sewer. So in order to allow cloudant sniffer invalidate the cooked cache, the node.js client and java client needs to share:
  * The cooked cache key convention
  * The memcache servers
  * The memcache sharding algorithm

Here are some settings we use in the memcached client:
  * Protocol: binary
  * Failure mode: cancel
  * Hash algorithm: KETAMA_HASH
  * No key compression

The failure mode "cancel" means the client will automatically cancel all operations heading towards a downed node. It won't redistribute the requests by taking the downed node out of the consistent hash ring. The reason is that in case there is intermittent network issue in the memcache server, we don't wish the new value is written to another server and when the server is available again, we read the dirty value. Of course this is a very edge case. The servers will be up most of the time, so we are okay to use some other policies.

The net.spy.memcached client will compress the payload to the cache if it's over 16k. We keep the default threshold. And we don't cache the resource if it's over 1M before compression because memcached is slow for huge resources.

The cooked cache can do the performance testing before deciding the policy of max cached payload size and whether to compress or not.
