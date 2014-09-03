import requests
import base64
import urlparse
import json

# fix bug 511: Update on Image structure in item-revision and offer-revision
# https://oculus.atlassian.net/browse/SER-511

############ CONFIGURE AREA ############
USER = ''
PASSWD = ''
PREFIX = ''
########################################

BASE_URL = 'https://%s.cloudant.com' % USER
AUTH = base64.encodestring(USER + ':' + PASSWD).strip()
headers = {
    'Content-Type': 'application/json',
    'Authorization': 'Basic %s' % AUTH
}
DESIGN_PREFIX = '_design/'

def save(name, data):
    with open('data/' + name, 'w') as data_file:
        data_file.write(data.encode('utf-8'))

def getRevisions(revision_type):
    url = urlparse.urljoin(BASE_URL, PREFIX + revision_type + '/_all_docs?include_docs=true&limit=2000')
    r = requests.get(url, headers=headers)
    save(revision_type, r.text)
    return json.loads(r.text)

def getEntities(entity_type):
    url = urlparse.urljoin(BASE_URL, PREFIX + entity_type + '/_all_docs?include_docs=true&limit=2000')
    r = requests.get(url, headers=headers)
    save(entity_type, r.text)
    return json.loads(r.text)

def changeImage(images, name):
    if name not in images:
        print 'no', name, ', skip'
        return
    img = images[name]
    if 'href' not in img and 'id' not in img:
        print 'no need to process', name, ', skip'
        return
    x, y = '15', '20'
    if 'width' in img and img['width'] != '':
        x = str(img['width'])
    if 'height' in img and img['height'] != '':
        y = str(img['height'])

    images[name] = { x + 'x' + y: img }

def updateEntity(entity, entity_type):
    url = urlparse.urljoin(BASE_URL, PREFIX + entity_type + '/' + entity['_id'])
    r = requests.put(url, json.dumps(entity), headers=headers)
    print r.status_code, r.text

def updateRevision(revision, revision_type):
    url = urlparse.urljoin(BASE_URL, PREFIX + revision_type + '/' + revision['_id'])
    r = requests.put(url, json.dumps(revision), headers=headers)
    print r.status_code, r.text

def changeImages(revision):
    if 'locales' not in revision:
        print 'no locales, skip'
        return False, None

    for locale in revision['locales']:
        localeProperties = revision['locales'][locale]
        if 'images' not in localeProperties:
            print 'no images, skip'
            continue
        images = localeProperties['images']
        changeImage(images, 'main')
        changeImage(images, 'thumbnail')
        changeImage(images, 'background')
        changeImage(images, 'featured')
        if 'gallery' in images:
            for gallery in images['gallery']:
                changeImage(gallery, 'full')
                changeImage(gallery, 'thumbnail')

        images.pop('halfMain', None)
        images.pop('halfThumbnail', None)
    return True, revision


def refreshEntity(entity_type):
    print 'processing', entity_type
    r = getEntities(entity_type)
    print entity_type, 'to process:', len(r['rows'])
    total = len(r['rows'])
    for row in r['rows']:
        total = total - 1
        if row['id'].startswith(DESIGN_PREFIX):
            print 'skip processing design doc', row['id']
            continue
        print 'processing', entity_type, row['id'], ', left', total
        if 'activeRevision' in row['doc']:
            ok, revision = changeImages(row['doc']['activeRevision'])
            if ok:
                row['doc']['activeRevision'] = revision
                updateEntity(row['doc'], entity_type)
    print 'finished processing', entity_type

def refreshRevision(revision_type):
    print 'processing', revision_type
    r = getRevisions(revision_type)
    print 'revisions to process:', len(r['rows'])
    total = len(r['rows'])
    for row in r['rows']:
        total = total - 1
        if row['id'].startswith(DESIGN_PREFIX):
            print 'skip processing design doc', row['id']
            continue
        print 'processing', revision_type, row['id'], ', left', total
        ok, revision = changeImages(row['doc'])
        if ok:
            updateRevision(revision, revision_type)
    print 'finished processing', revision_type

def main():
    refreshRevision('item_revision')
    #refreshRevision('offer_revision')
    #refreshEntity('item')
    #refreshEntity('offer')

main()