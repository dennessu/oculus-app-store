/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.common.AgeRating;
import com.junbo.catalog.spec.model.common.ImageGalleryEntry;
import com.junbo.catalog.spec.model.item.*;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.store.spec.model.browse.AddReviewRequest;
import com.junbo.store.spec.model.browse.Images;
import com.junbo.store.spec.model.browse.ListResponse;
import com.junbo.store.spec.model.browse.SectionLayoutResponse;
import com.junbo.store.spec.model.browse.document.*;
import com.junbo.store.spec.model.external.sewer.casey.CaseyAggregateRating;
import com.junbo.store.spec.model.external.sewer.casey.CaseyReview;
import com.junbo.test.catalog.enums.PriceType;
import com.junbo.test.common.Validator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.testng.Assert;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The StoreBrowseValidationHelper class.
 */
public class StoreBrowseValidationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreBrowseValidationHelper.class);

    private static final String Platform = "ANDROID";

    private static final String locale = "en_US";

    private static final String country = "US";

    private static final Pattern ImageDimensionTextPattern = Pattern.compile("\\s*(\\d+)\\s*[xX]\\s*(\\d+)\\s*");

    private static final TreeMap<Integer, String> lengthToImageSizeGroup = new TreeMap<>();

    private static final List<String> SizeText_Details_Main = Arrays.asList("1440x810", "230x129");

    private static final List<String> SizeText_Details_Gallery_Full = Arrays.asList("2560x1440");

    private static final List<String> SizeText_Details_Gallery_Thumbnail = Arrays.asList("1360x765", "336x189");

    private static final List<String> SizeText_List_Thumbnail = Arrays.asList("1440x810", "690x388", "216x122");

    private StoreTestDataProvider storeTestDataProvider;

    interface VerifyEqual<T> {
        void verify(T o1, T o2);
    }

    static {
        lengthToImageSizeGroup.put(2560, "large");
        lengthToImageSizeGroup.put(1440, "medium");
        lengthToImageSizeGroup.put(690, "small");
        lengthToImageSizeGroup.put(336, "tiny");
    }

    public StoreBrowseValidationHelper(StoreTestDataProvider storeTestDataProvider) {
        this.storeTestDataProvider = storeTestDataProvider;
    }

    public void verifySectionLayoutBreadcrumbs(SectionLayoutResponse layout, SectionLayoutResponse parentLayout, SectionInfo parentSection) throws Exception {
        if (parentLayout != null) {
            Assert.assertEquals(layout.getBreadcrumbs().size(), parentLayout.getBreadcrumbs().size() + 1, "Breadcrumbs length not corret");
            for (int i = 0; i < parentLayout.getBreadcrumbs().size(); ++i) {
                Validator.Validate("node in breadcrumbs not equal", layout.getBreadcrumbs().get(i), parentLayout.getBreadcrumbs().get(i));
            }
        }

        if (parentSection != null) {
            SectionInfo last = layout.getBreadcrumbs().get(layout.getBreadcrumbs().size() - 1);
            Assert.assertEquals(last.getCriteria(), parentSection.getCriteria());
            Assert.assertEquals(last.getCategory(), parentSection.getCategory());
            Assert.assertEquals(last.getName(), parentSection.getName());
        }
    }

    public void verifyItemsInList(List<String> names, final List<com.junbo.store.spec.model.browse.document.Item> items, boolean verifySameSize) {
        for (final String name : names) {
            Object result = CollectionUtils.find(items, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    com.junbo.store.spec.model.browse.document.Item item = (com.junbo.store.spec.model.browse.document.Item) object;
                    return name.equals(item.getTitle());
                }
            });
            Assert.assertNotNull(result, String.format("Item %s not in the list.", name));
        }

        if (verifySameSize) {
            Assert.assertEquals(names.size(), items.size(), "Size of list not equals.");
        }
    }

    public void verifyReview(Review review, CaseyReview caseyReview, String expectedName) {
        Assert.assertEquals(review.getAuthorName(), expectedName);
        Assert.assertEquals(review.getContent(), caseyReview.getReview());
        Assert.assertEquals(review.getTitle(), caseyReview.getReviewTitle());
        Assert.assertEquals(review.getStarRatings().size(), caseyReview.getRatings().size());
        for (CaseyReview.Rating rating : caseyReview.getRatings()) {
            Assert.assertEquals(review.getStarRatings().get(rating.getType()).intValue(), rating.getScore() / 20, "rating result not correct");
        }
    }

    public void verifyAggregateRatingsBasic(Map<String, AggregatedRatings> aggregatedRatings) {
        Assert.assertNotNull(aggregatedRatings);
        Assert.assertEquals(aggregatedRatings.keySet(), new HashSet<>(Arrays.asList("comfort", "quality")));
        for (AggregatedRatings ratings : aggregatedRatings.values()) {
            Assert.assertNotNull(ratings);
            Assert.assertNull(ratings.getCommentsCount());
            Assert.assertTrue(ratings.getAverageRating() >= 0.0);
            Assert.assertTrue(ratings.getRatingsCount() >= 0);
            Assert.assertEquals(ratings.getRatingsHistogram().size(), 5);
            for (Long val : ratings.getRatingsHistogram().values()) {
                Assert.assertTrue(val >= 0L);
            }
        }
    }

    public void verifyAggregateRatings(Map<String, AggregatedRatings> aggregatedRatings, List<CaseyAggregateRating> caseyAggregatedRatings) {
        verifyAggregateRatings(aggregatedRatings, caseyAggregatedRatings, new HashSet<String>());
    }

    public void verifyAggregateRatings(Map<String, AggregatedRatings> aggregatedRatings, List<CaseyAggregateRating> caseyAggregatedRatings, Set<String> defaultRating) {
        for (final Map.Entry<String, AggregatedRatings> entry : aggregatedRatings.entrySet()) {
            if (defaultRating.contains(entry.getKey())) {
                verifyDefaultAggregateRatings(entry.getValue());
                continue;
            }
            CaseyAggregateRating caseyAggregateRating = (CaseyAggregateRating) CollectionUtils.find(caseyAggregatedRatings, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return ObjectUtils.nullSafeEquals(((CaseyAggregateRating) object).getType(), entry.getKey());
                }
            });
            AggregatedRatings rating = entry.getValue();
            Assert.assertEquals(rating.getAverageRating(), caseyAggregateRating.getAverage() / 20, 0.00001, "average rating not correct");
            Assert.assertEquals(rating.getRatingsCount(), caseyAggregateRating.getCount(), "rating count not correct");
            Assert.assertNull(rating.getCommentsCount(), "comments count should be null");

            for (int i = 0; i < caseyAggregateRating.getHistogram().length; i += 2) {
                Assert.assertEquals(rating.getRatingsHistogram().get(i / 2).longValue(),
                        (caseyAggregateRating.getHistogram()[i] + caseyAggregateRating.getHistogram()[i + 1]), "rating histogram not correct");
            }
        }
    }

    public void verifyItem(com.junbo.store.spec.model.browse.document.Item item, boolean serviceClientEnabled, boolean verifyAttributes,
                           ApiEndPoint apiEndPoint) throws Exception {
        boolean isList = apiEndPoint == ApiEndPoint.List;
        boolean offerAvailable = apiEndPoint != ApiEndPoint.Library && apiEndPoint != ApiEndPoint.Purchase;
        com.junbo.catalog.spec.model.offer.Offer catalogOffer = null;
        if (offerAvailable) {
            catalogOffer = storeTestDataProvider.getOfferByOfferId(item.getOffer().getSelf().getValue(), true);
        }
        com.junbo.catalog.spec.model.item.Item catalogItem = storeTestDataProvider.getItemByItemId(item.getSelf().getValue(), true);
        if ((offerAvailable && catalogOffer == null) || catalogItem == null) {
            LOGGER.info("Offer/Item not found, skip verify item:{}", item.getSelf());
            return;
        }

        OfferRevision currentOfferRevision = null;
        OfferRevisionLocaleProperties offerLocaleProperties = null;
        Organization publisher = null;
        if (offerAvailable) {
            currentOfferRevision = storeTestDataProvider.getOfferRevision(item.getOffer().getCurrentRevision().getValue());
            offerLocaleProperties = currentOfferRevision.getLocales().get(locale);
            publisher = getOrganization(catalogOffer.getOwnerId(), serviceClientEnabled);
        }
        ItemRevision currentItemRevision = storeTestDataProvider.getItemRevision(item.getCurrentRevision().getValue());
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = currentItemRevision.getLocales().get(locale);
        Organization developer = getOrganization(catalogItem.getOwnerId(), serviceClientEnabled);

        List<OfferAttribute> offerAttributes = new ArrayList<>();
        List<ItemAttribute> itemAttributes = new ArrayList<>();
        List<ItemRevision> itemRevisions = Arrays.asList(currentItemRevision);//getItemRevisions(catalogItem); // todo may return all the item revisions
        if (ApiEndPoint.Details == apiEndPoint) {
            itemRevisions = getItemRevisionForRevisionNote(item.getSelf());
        }

        if (offerAvailable) {
            if (!org.springframework.util.CollectionUtils.isEmpty(catalogOffer.getCategories())) {
                for (String id : catalogOffer.getCategories()) {
                    offerAttributes.add(storeTestDataProvider.getOfferAttribute(id));
                }
            }
        } else {
            if (!org.springframework.util.CollectionUtils.isEmpty(catalogItem.getCategories())) {
                for (String id : catalogItem.getCategories()) {
                    offerAttributes.add(storeTestDataProvider.getOfferAttribute(id));
                }
            }
        }
        if (!org.springframework.util.CollectionUtils.isEmpty(catalogItem.getGenres())) {
            for (String id : catalogItem.getGenres()) {
                itemAttributes.add(storeTestDataProvider.getItemAttribute(id));
            }
        }

        verifyItem(item, catalogItem, currentItemRevision, currentOfferRevision, developer, serviceClientEnabled, offerAvailable);
        verifyItemImages(item.getImages(), offerAvailable ? offerLocaleProperties.getImages() : itemRevisionLocaleProperties.getImages(), isList);
        verifyAppDetails(item.getAppDetails(), offerAttributes, itemAttributes, currentOfferRevision, currentItemRevision, itemRevisions,
                developer, publisher, serviceClientEnabled, verifyAttributes, offerAvailable);

        if (offerAvailable) {
            boolean isFree = PriceType.FREE.name().equals(currentOfferRevision.getPrice().getPriceType());
            verifyItemOffer(item.getOffer(), catalogOffer, currentOfferRevision, isFree);
        }

        verifyAggregateRatingsBasic(item.getAggregatedRatings());
    }

    public void validateCmsSection(SectionInfoNode sectionInfoNode, String expectedName, String expectedCriteria) {
        Assert.assertEquals(sectionInfoNode.getCriteria(), expectedCriteria);
        Assert.assertEquals(sectionInfoNode.getName(), expectedName);
        Assert.assertNull(sectionInfoNode.getCategory());
    }

    public void validateCmsSection(SectionInfo sectionInfo, String expectedName, String expectedCriteria) {
        Assert.assertEquals(sectionInfo.getCriteria(), expectedCriteria);
        Assert.assertEquals(sectionInfo.getName(), expectedName);
        Assert.assertNull(sectionInfo.getCategory());
    }

    public void validateCmsSection(SectionLayoutResponse sectionLayoutResponse, String name, int numOfItems, boolean hasMoreItems) {
        Assert.assertEquals(sectionLayoutResponse.getName(), name);
    }

    public void getAndValidateItemList(String category, String criteria, String cursor, int pageSize, int expectedOfItems, boolean hasNext) throws Exception {
        ListResponse response = storeTestDataProvider.getList(category, criteria, cursor, pageSize);
        Assert.assertEquals(response.getItems().size(), expectedOfItems);
        if (hasNext) {
            Assert.assertNotNull(response.getNext().getCursor());
            Assert.assertEquals(response.getNext().getCriteria(), criteria);
            Assert.assertEquals(response.getNext().getCategory(), category);
            Assert.assertEquals(response.getNext().getCount().intValue(), pageSize);
        } else {
            Assert.assertNull(response.getNext());
        }
    }


    public void validateCmsTopLevelSectionLayout(SectionLayoutResponse sectionLayoutResponse, int expectedNumOfChild, String expectedName) {
        Assert.assertTrue(sectionLayoutResponse.getBreadcrumbs().isEmpty(), "top level section's breadcrumbs should be empty");
        Assert.assertEquals(sectionLayoutResponse.getChildren().size(), expectedNumOfChild);
        Assert.assertEquals(sectionLayoutResponse.getName(), expectedName);
    }

    public void verifyDefaultAggregateRatings(Map<String, AggregatedRatings> aggregatedRatingsMap) {
        Assert.assertEquals(aggregatedRatingsMap.keySet(), new HashSet<>(Arrays.asList("comfort", "quality")));
        for (AggregatedRatings ratings : aggregatedRatingsMap.values()) {
            verifyDefaultAggregateRatings(ratings);
        }
    }

    public void verifyDefaultAggregateRatings(AggregatedRatings aggregatedRating) {
        Assert.assertNull(aggregatedRating.getCommentsCount());
        Assert.assertEquals(aggregatedRating.getAverageRating(), 0.0, 0.00001);
        Assert.assertEquals(aggregatedRating.getRatingsCount(), new Long(0));
        Map<Integer, Long> ratingsHistogram = new HashMap<>();
        for (int i = 0; i < 5; ++i) {
            ratingsHistogram.put(i, 0L);
        }
        Assert.assertEquals(aggregatedRating.getRatingsHistogram(), ratingsHistogram);
    }

    public void validateAddReview(AddReviewRequest addReviewRequest, Review review, String nickName) {
        Assert.assertEquals(review.getContent(), addReviewRequest.getContent());
        Assert.assertEquals(review.getTitle(), addReviewRequest.getTitle());
        Assert.assertEquals(review.getAuthorName(), nickName);
        Assert.assertEquals(review.getStarRatings(), addReviewRequest.getStarRatings());
        Assert.assertNotNull(review.getSelf());
        Assert.assertNotNull(review.getTimestamp());
    }

    public void verifyImage(Images images, Map<String, String> mainImageUrls, List<Map<String, String>> galleries) {
        Assert.assertEquals(images.getMain().keySet(), mainImageUrls.keySet());
        for (Map.Entry<String, String> entry : mainImageUrls.entrySet()) {
            Assert.assertTrue(images.getMain().get(entry.getKey()).getImageUrl().contains(entry.getValue()));
        }
        Assert.assertEquals(images.getGallery().size(), galleries.size());
        for (int i = 0; i < galleries.size(); ++i) {
            Assert.assertEquals(images.getGallery().get(i).keySet(), galleries.get(i).keySet());
            for (Map.Entry<String, String> entry : galleries.get(i).entrySet()) {
                Assert.assertTrue(images.getGallery().get(i).get(entry.getKey()).getImageUrl().contains(entry.getValue()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void verifyItem(com.junbo.store.spec.model.browse.document.Item item, Item catalogItem, ItemRevision itemRevision,
                            OfferRevision offerRevision,
                            Organization developer, boolean serviceClientEnabled, boolean offerAvailable) {
        Assert.assertEquals(item.getSelf(), new ItemId(catalogItem.getItemId()));
        Assert.assertEquals(item.getItemType(), catalogItem.getType());
        OfferRevisionLocaleProperties offerLocaleProperties = !offerAvailable ? null : offerRevision.getLocales().get(locale);
        ItemRevisionLocaleProperties itemLocaleProperties = itemRevision.getLocales().get(locale);
        Assert.assertEquals(item.getTitle(), defaultIfNull(offerAvailable ? offerLocaleProperties.getName() : itemLocaleProperties.getName()),
                "item title not match");
        Assert.assertEquals(item.getDescriptionHtml(), defaultIfNull(offerAvailable ? offerLocaleProperties.getLongDescription() : itemLocaleProperties.getLongDescription()),
                "description html not match");
        verifySupportedLocaleEquals(item.getSupportedLocales(), defaultIfNull(itemRevision.getSupportedLocales()));

        if (serviceClientEnabled) {
            Assert.assertEquals(item.getCreator(), defaultIfNull(developer == null ? null : developer.getName()));
        }
    }

    private void verifyItemImages(Images images, com.junbo.catalog.spec.model.common.Images catalogImages, boolean isList) {
        if (catalogImages == null) {
            Assert.assertEquals(images.getGallery(), new ArrayList<Map<String, Image>>());
            Assert.assertEquals(images.getMain(), new HashMap<String, Image>());
            return;
        }
        verifyImageEquals(images.getMain(), buildExpectedImage(catalogImages, isList));
        if (isList) {
            Assert.assertNull(images.getGallery());
        } else {
            if (catalogImages.getGallery() == null) {
                Assert.assertEquals(images.getGallery().size(), 0);
            } else {
                Assert.assertEquals(images.getGallery().size(), catalogImages.getGallery().size());
                for (int i = 0; i < images.getGallery().size(); ++i) {
                    verifyImageEquals(images.getGallery().get(i), buildExpectedImage(catalogImages.getGallery().get(i)));
                }
            }
        }
    }

    private void verifyAppDetails(AppDetails appDetails, List<OfferAttribute> categories, List<ItemAttribute> genres,
                                  OfferRevision offerRevision, ItemRevision itemRevision, List<ItemRevision> itemRevisions,
                                  Organization developer, Organization publisher, boolean serviceClientEnabled, boolean verifyAttributes,
                                  boolean offerAvailable) throws Exception {
        // verify categories
        ItemRevisionLocaleProperties localeProperties = itemRevision.getLocales().get(locale);
        if (verifyAttributes) {
            if (categories == null) {
                Assert.assertTrue(appDetails.getCategories().isEmpty());
            } else {
                Assert.assertEquals(appDetails.getCategories().size(), categories.size());
                for (int i = 0; i < appDetails.getCategories().size(); ++i) {
                    Assert.assertEquals(appDetails.getCategories().get(i).getId(), categories.get(i).getId());
                    Assert.assertEquals(appDetails.getCategories().get(i).getName(), categories.get(i).getLocales().get(locale).getName());
                }
            }
        }

        // verify genres
        if (verifyAttributes) {
            if (genres == null) {
                Assert.assertTrue(appDetails.getGenres().isEmpty());
            } else {
                Assert.assertEquals(appDetails.getGenres().size(), genres.size());
                for (int i = 0; i < appDetails.getGenres().size(); ++i) {
                    Assert.assertEquals(appDetails.getGenres().get(i).getId(), genres.get(i).getId());
                    Assert.assertEquals(appDetails.getGenres().get(i).getName(), genres.get(i).getLocales().get(locale).getName());
                }
            }
        }

        if (serviceClientEnabled) {
            Assert.assertEquals(appDetails.getPublisherName(), defaultIfNull(publisher == null ? null : publisher.getName()));
            Assert.assertEquals(appDetails.getDeveloperName(), defaultIfNull(developer == null ? null : developer.getName()));
        }

        Assert.assertEquals(appDetails.getWebsite(), localeProperties.getWebsite());
        Assert.assertEquals(appDetails.getForumUrl(), localeProperties.getCommunityForumLink());
        Assert.assertEquals(appDetails.getDeveloperEmail(), defaultIfNull(localeProperties.getSupportEmail()));
        Assert.assertEquals(appDetails.getPackageName(), defaultIfNull(itemRevision.getPackageName()));

        if (itemRevision.getBinaries() != null && itemRevision.getBinaries().get(Platform) != null) {
            Binary binary = itemRevision.getBinaries().get(Platform);
            Assert.assertEquals(appDetails.getVersionString(), defaultIfNull(binary.getVersion()));
            Assert.assertEquals(appDetails.getInstallationSize(), defaultIfNull(binary.getSize()));
            Assert.assertEquals(appDetails.getVersionCode(), defaultIfNull(binary.getMetadata() == null
                    || binary.getMetadata().get("versionCode") == null ? null : binary.getMetadata().get("versionCode").asInt()));
        }

        if (!offerAvailable) {
            Assert.assertNull(appDetails.getReleaseDate());
        } else {
            if (offerRevision.getCountries() == null || offerRevision.getCountries().get(country) == null ||
                    offerRevision.getCountries().get(country).getReleaseDate() == null) {
                Assert.assertEquals(appDetails.getReleaseDate(), defaultIfNull((Date) null));
            } else {
                Assert.assertEquals(appDetails.getReleaseDate(), offerRevision.getCountries().get(country).getReleaseDate());
            }
        }

        // verify release notes
        verifyRevisionNote(appDetails.getRevisionNotes(), itemRevisions, offerAvailable);

        JsonNode jsonNode = itemRevision.getBinaries().get(Platform).getMetadata().get("permissions");
        Assert.assertEquals(appDetails.getPermissions(), jsonNode == null || jsonNode.isNull() ?
                new ArrayList<String>() :
                ObjectMapperProvider.instance().readValue(jsonNode.traverse(), new TypeReference<List<String>>() {}));
        Assert.assertNull(appDetails.getDeveloperWebsite());
        Assert.assertNull(appDetails.getPublisherEmail());
        Assert.assertNull(appDetails.getPublisherWebsite());
    }

    private void verifyRevisionNote(List<RevisionNote> revisionNotes, List<ItemRevision> itemRevisions, boolean offerAvailable) {
        // verify release notes
        List<ItemRevision> revisions = new ArrayList<>(itemRevisions);
        Collections.sort(revisions, new Comparator<ItemRevision>() {
            @Override
            public int compare(ItemRevision o1, ItemRevision o2) {
                return o2.getUpdatedTime().compareTo(o1.getUpdatedTime());
            }
        });
        Assert.assertEquals(revisionNotes.size(), revisions.size());
        for (int i = 0; i < revisionNotes.size(); ++i) {
            RevisionNote revisionNote = revisionNotes.get(i);
            ItemRevision historyItemRevision = revisions.get(i);
            ItemRevisionLocaleProperties historyLocalProperties = historyItemRevision.getLocales().get(locale);
            Binary historyBinary = historyItemRevision.getBinaries() == null ? null : historyItemRevision.getBinaries().get(Platform);

            Assert.assertEquals(revisionNote.getReleaseDate(), itemRevisions.get(i).getUpdatedTime());
            Assert.assertEquals(revisionNote.getTitle(),
                    defaultIfNull(historyLocalProperties == null ? null : historyLocalProperties.getReleaseNotes().getShortNotes()));
            Assert.assertEquals(revisionNote.getDescription(),
                    defaultIfNull(historyLocalProperties == null ? null : historyLocalProperties.getReleaseNotes().getLongNotes()));
            Assert.assertEquals(revisionNote.getVersionCode(), defaultIfNull(historyBinary == null || historyBinary.getMetadata() == null
                    || historyBinary.getMetadata().get("versionCode") == null ? null : historyBinary.getMetadata().get("versionCode").asInt()));
            Assert.assertEquals(revisionNote.getVersionString(), defaultIfNull(historyBinary == null ? null : historyBinary.getVersion()));
        }
    }

    private void verifyItemOffer(com.junbo.store.spec.model.browse.document.Offer offer,
                                 com.junbo.catalog.spec.model.offer.Offer catalogOffer, OfferRevision offerRevision, boolean isFree) {
        Assert.assertEquals(offer.getSelf(), new OfferId(catalogOffer.getId()));
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = offerRevision.getLocales() == null ? null : offerRevision.getLocales().get(locale);
        Assert.assertEquals(offer.getFormattedDescription(), defaultIfNull(offerRevisionLocaleProperties == null ? null : offerRevisionLocaleProperties.getShortDescription()));
        Assert.assertEquals(offer.getIsFree().booleanValue(), isFree);
        if (isFree) {
            Assert.assertEquals(offer.getPrice().doubleValue(), BigDecimal.ZERO.doubleValue(), 0.00001);
        }
        Assert.assertEquals(offer.getCurrency().getValue(), "USD");
    }

    private String getImageSizeGroup(String dimensionText) {
        Matcher matcher = ImageDimensionTextPattern.matcher(dimensionText);
        matcher.matches();
        int width = Integer.parseInt(matcher.group(1));
        int height = Integer.parseInt(matcher.group(2));
        int length = Math.max(width, height);
        Map.Entry<Integer, String> entry = lengthToImageSizeGroup.ceilingEntry(length);
        if (entry == null) {
            entry = lengthToImageSizeGroup.lastEntry();
        }
        return entry.getValue();
    }

    private void verifyImageEquals(Map<String, Image> actual, Map<String, Image> expected) {
        if (expected == null) {
            Assert.assertNull(actual);
        }
        Assert.assertEquals(actual.keySet(), expected.keySet());
        for (Map.Entry<String, Image> entry: actual.entrySet()) {
            Assert.assertEquals(entry.getValue().getImageUrl(), expected.get(entry.getKey()).getImageUrl());
            Assert.assertEquals(entry.getValue().getAltText(), expected.get(entry.getKey()).getAltText());
        }
    }

    private Map<String, Image> buildExpectedImage(com.junbo.catalog.spec.model.common.Images catalogImages, boolean isList) {
        Map<String, Image> result = new HashMap<>();
        if (isList) {
            buildImageMap(SizeText_List_Thumbnail, catalogImages.getThumbnail(), result);
        } else {
            buildImageMap(SizeText_Details_Main, catalogImages.getMain(), result);
        }
        return result;
    }

    private Map<String, Image> buildExpectedImage(ImageGalleryEntry galleryEntry) {
        Map<String, Image> result = new HashMap<>();
        buildImageMap(SizeText_Details_Gallery_Thumbnail, galleryEntry.getThumbnail(), result);
        buildImageMap(SizeText_Details_Gallery_Full, galleryEntry.getFull(), result);
        return result;
    }

    private void buildImageMap(List<String> sizeTextList, Map<String, com.junbo.catalog.spec.model.common.Image> catalogImageMap, Map<String, Image> result) {
        Assert.assertNotNull(result);
        for (String sizeText:sizeTextList) {
            if (catalogImageMap != null && catalogImageMap.get(sizeText) != null) {
                result.put(getImageSizeGroup(sizeText), buildImage(catalogImageMap.get(sizeText)));
            }
        }
    }

    private Image buildImage(com.junbo.catalog.spec.model.common.Image catalogImage) {
        Image storeImage = new Image();
        storeImage.setAltText(catalogImage.getAltText());
        storeImage.setImageUrl(catalogImage.getHref());
        return storeImage;
    }

    private void verifyImageMap(Map<String, Image> storeImageMap, Map<String, com.junbo.catalog.spec.model.common.Image>... catalogImageMaps) {
        Map<String, Image> expected = new HashMap<>();
        for (Map<String, com.junbo.catalog.spec.model.common.Image> catalogImageMap : catalogImageMaps) {
            for (Map.Entry<String, com.junbo.catalog.spec.model.common.Image> entry : catalogImageMap.entrySet()) {
                try {
                    String groupName = getImageSizeGroup(entry.getKey());
                    Image storeImage = new Image();
                    storeImage.setAltText(entry.getValue().getAltText());
                    storeImage.setImageUrl(entry.getValue().getHref());
                    if (expected.get(groupName) == null) {
                        expected.put(groupName, storeImage);
                    }
                } catch (Exception ex) {
                }
            }
        }

        Assert.assertEquals(storeImageMap.keySet(), expected.keySet());
        for (Map.Entry<String, Image> entry : storeImageMap.entrySet()) {
            Assert.assertEquals(entry.getValue().getAltText(), expected.get(entry.getKey()).getAltText(), "altText not equal");
            Assert.assertEquals(entry.getValue().getImageUrl(), expected.get(entry.getKey()).getImageUrl(), "image url not equal");
        }
    }


    private List<OfferRevision> getOfferRevisions(com.junbo.catalog.spec.model.offer.Offer offer) throws Exception {
        HashMap<String, List<String>> params = new HashMap<>();
        params.put("offerId", Collections.singletonList(offer.getId()));
        params.put("status", Collections.singletonList("APPROVED"));
        Results<OfferRevision> revisionResults = storeTestDataProvider.offerRevisionClient.getOfferRevisions(params);
        return revisionResults.getItems();
    }

    private <T> void verifyEquals(Map<String, T> o1, Map<String, T> o2, VerifyEqual<T> func) {
        if (o2 == null) {
            Assert.assertNull(o1);
            return;
        }
        Assert.assertEquals(o1.size(), o2.size());
        Assert.assertEquals(o1.keySet(), o2.keySet());
        for (Map.Entry<String, T> entry : o1.entrySet()) {
            func.verify(entry.getValue(), o2.get(entry.getKey()));
        }
    }

    private void verifySupportedLocaleEquals(Map<String, SupportedLocale> o1, Map<String, SupportedLocale> o2) {
        verifyEquals(o1, o2, new VerifyEqual<SupportedLocale>() {
            @Override
            public void verify(SupportedLocale o1, SupportedLocale o2) {
                Assert.assertEquals(o1.isAudio(), o2.isAudio());
                Assert.assertEquals(o1.isInterfac3(), o2.isInterfac3());
                Assert.assertEquals(o1.isSubtitles(), o2.isSubtitles());
            }
        });
    }

    private void verifyAgeRatingEquals(Map<String, List<AgeRating>> o1, Map<String, List<AgeRating>> o2) {
        verifyEquals(o1, o2, new VerifyEqual<List<AgeRating>>() {
            @Override
            public void verify(List<AgeRating> o1, List<AgeRating> o2) {
                Assert.assertEquals(o1.size(), o2.size());
                for (int i = 0; i < o1.size(); ++i) {
                    AgeRating a1 = o1.get(i);
                    AgeRating a2 = o2.get(i);
                    Assert.assertEquals(a1.getCategory(), a2.getCategory());
                    Assert.assertEquals(a1.getCertificate(), a2.getCertificate());
                    Assert.assertEquals(a1.getDescriptors(), a2.getDescriptors());
                    Assert.assertEquals(a1.getOnline(), a2.getOnline());
                    Assert.assertEquals(a1.getProvisional(), a2.getProvisional());
                }
            }
        });
    }

    private List<ItemRevision> getItemRevisionForRevisionNote(ItemId itemId) throws Exception {
        String platform = "ANDROID";
        int revisionNum = 10;
        List<ItemRevision> itemRevisions = storeTestDataProvider.getItemRevisions(itemId);
        TreeMap<Integer, ItemRevision> revisionTreeMap = new TreeMap<>();
        for (ItemRevision itemRevision : itemRevisions) {
            if (itemRevision.getBinaries() != null && itemRevision.getBinaries().get(platform) != null &&
                    itemRevision.getBinaries().get(platform).getMetadata() != null &&
                    itemRevision.getBinaries().get(platform).getMetadata().get("versionCode") != null) {
                Integer versionCode = itemRevision.getBinaries().get(platform).getMetadata().get("versionCode").asInt();
                if (revisionTreeMap.get(versionCode) == null ||
                        revisionTreeMap.get(versionCode).getUpdatedTime().before(itemRevision.getUpdatedTime())) {
                    revisionTreeMap.put(versionCode, itemRevision);
                    if (revisionTreeMap.size() > revisionNum) {
                        revisionTreeMap.pollFirstEntry();
                    }
                }
            }
        }


        List<ItemRevision> result = new ArrayList<>();
        for (Map.Entry<Integer, ItemRevision> entry : revisionTreeMap.entrySet()) {
            result.add(entry.getValue());
        }

        Collections.reverse(result);
        return result;
    }

    private Organization getOrganization(OrganizationId organizationId, boolean serviceClientEnabled) {
        if (!serviceClientEnabled) {
            return null;
        }
        try {
            return storeTestDataProvider.getOrganization(organizationId, 0);
        } catch (Exception e) {
            LOGGER.error("name=Get_Organization_Error", e);
            return null;
        }
    }

    private String defaultIfNull(String val) {
        if (val == null) {
            return "";
        }
        return val;
    }

    private Integer defaultIfNull(Integer val) {
        if (val == null) {
            return 0;
        }
        return val;
    }

    private Long defaultIfNull(Long val) {
        if (val == null) {
            return 0L;
        }
        return val;
    }

    private Date defaultIfNull(Date val) throws ParseException {
        if (val == null) {
            return new ISO8601DateFormat().parse("1900-01-01T00:00:00Z");
        }
        return val;
    }

    private Map defaultIfNull(Map val) {
        if (val == null) {
            return Collections.emptyMap();
        }
        return val;
    }
}
