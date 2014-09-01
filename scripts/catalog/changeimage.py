import requests
import base64
import urlparse
import json

# fix bug 511: Update on Image structure in item-revision and offer-revision
# https://oculus.atlassian.net/browse/SER-511

############ CONFIGURE AREA ############
USER = 'silkcloudtest-asia'
PASSWD = '#Bugsfor$'
PREFIX = 'bz_'
########################################

BASE_URL = 'https://%s.cloudant.com' % USER
AUTH = base64.encodestring(USER + ':' + PASSWD).strip()
headers = {
    'Content-Type': 'application/json',
    'Authorization': 'Basic %s' % AUTH
}
DESIGN_PREFIX = '_design/'

def getRevisions(revision_type):
    url = urlparse.urljoin(BASE_URL, PREFIX + revision_type + '/_all_docs?include_docs=true')
    r = requests.get(url, headers=headers)
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

def updateRevision(revision, revision_type):
    url = urlparse.urljoin(BASE_URL, PREFIX + revision_type + '/' + revision['_id'])
    r = requests.put(url, json.dumps(revision), headers=headers)
    print r.status_code, r.text

def changeImages(revision, revision_type):
    if 'locales' not in revision:
        print 'no locales, skip'
        return

    for locale in revision['locales']:
        localeProperties = revision['locales'][locale]
        if 'images' not in localeProperties:
            print 'no images, skip'
            return
        images = localeProperties['images']
        changeImage(images, 'main')
        changeImage(images, 'thumbnail')
        changeImage(images, 'background')
        changeImage(images, 'featured')
        if 'gallery' in images:
            changeImage(images['gallery'], 'full')
            changeImage(images['gallery'], 'thumbnail')

        images.pop('halfMain', None)
        images.pop('halfThumbnail', None)
    updateRevision(revision, revision_type)

def main():
    for revision_type in ['item_revision', 'offer_revision']:
        print 'processing', revision_type
        r = getRevisions('item_revision')
        print 'revisions to process:', len(r['rows'])
        for row in r['rows']:
            if row['id'].startswith(DESIGN_PREFIX):
                print 'skip processing design doc', row['id']
                continue
            print 'processing', revision_type, row['id']
            changeImages(row['doc'], 'item_revision')
        print 'finished processing', revision_type

main()