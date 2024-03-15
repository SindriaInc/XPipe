package org.cmdbuild.gis;

import org.cmdbuild.gis.geoserver.GeoserverLayerImpl;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Multimaps.index;
import static java.lang.String.format;
import java.util.List;

import org.cmdbuild.config.GisConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.CmdbFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.gis.geoserver.GeoserverService;
import static org.cmdbuild.gis.utils.GisUtils.cmGeometryToPostgisSql;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeService;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.gis.geoserver.GisGeoserverLayerRepository;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerExt;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;

@Component("gisService")
public class GisServiceImpl implements GisService, MinionComponent {

    private static final String DOMAIN_TREE_TYPE = "gisnavigation";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final GisAttributeRepository gisAttributeRepository;
    private final GisValueRepository gisValueRepository;
    private final NavTreeService domainTreeStore;
    private final GisConfiguration configuration;
    private final GeoserverService geoserverService;
    private final GisGeoserverLayerRepository geoserverLayerRepository;
    private final UserClassService userClassService;

    private final MinionHandlerExt minionHandler;

    public GisServiceImpl(NavTreeService domainTreeRepository, GisGeoserverLayerRepository layerRepository, DaoService dao, GisValueRepository geoFeatureStore, GisConfiguration configuration, GeoserverService geoServerService, GisAttributeRepository layerStore, UserClassService userClassService) {
        this.dao = checkNotNull(dao);
        this.gisAttributeRepository = checkNotNull(layerStore);
        this.domainTreeStore = checkNotNull(domainTreeRepository);
        this.gisValueRepository = checkNotNull(geoFeatureStore);
        this.configuration = checkNotNull(configuration);
        this.geoserverService = checkNotNull(geoServerService);
        this.geoserverLayerRepository = checkNotNull(layerRepository);
        this.userClassService = checkNotNull(userClassService);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("GIS Service")
                .withEnabledChecker(this::isGisEnabled)
                .reloadOnConfigs(GisConfiguration.class)
                .build();
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public void start() {
        logger.debug("checkGisSchema");
        try {
            gisValueRepository.checkGisSchemaAndCreateIfMissing();
            minionHandler.setStatus(MRS_READY);
        } catch (Exception ex) {
            minionHandler.setStatus(MRS_ERROR);
            logger.error(marker(), "error checking gis schema", ex);
        }
    }

    @Override
    public void stop() {
        minionHandler.setStatus(MRS_NOTRUNNING);
    }

    @Override
    public boolean isGisEnabled() {
        return configuration.isEnabled();
    }

    @Override
    public boolean isGeoserverEnabled() {
        return configuration.isGeoServerEnabled();
    }

    @Override
    @Transactional
    public GisAttribute createGisAttribute(GisAttribute gisAttribute) {
        checkGisEnabled();
        if (gisAttribute.isPostgis()) {
            gisValueRepository.createGisTable(gisAttribute);
        }
        return gisAttributeRepository.create(gisAttribute);
    }

    @Override
    public GisAttribute updateGisAttribute(GisAttribute layer) {
        checkGisEnabled();
        return gisAttributeRepository.update(layer);
    }

    @Override
    @Transactional
    public void deleteGisAttribute(String classId, String attributeName) {
        checkGisEnabled();
        GisAttribute gisAttribute = gisAttributeRepository.get(classId, attributeName);
        if (gisAttribute.isPostgis()) {
            gisValueRepository.deleteGisTable(classId, attributeName);
        }
        gisAttributeRepository.delete(classId, attributeName);
    }

    @Override
    public List<GisValue> getGisValues(String classId, long cardId) {
        return getGisValues(dao.getClasse(classId), cardId);
    }

    @Override
    public List<GisValue> getGisValuesForCurrentUser(String classId, long cardId) {
        return getGisValues(userClassService.getUserClass(classId), cardId);
    }

    @Override
    public List<GisValue> getGisValues(Collection<Long> layers, String bbox, CmdbFilter filter, String forOwner) {
        checkGisEnabled();
        List<GisValue> values = gisValueRepository.getGisValues(layers, bbox);
        values = filterGisValues(values, filter, forOwner);
        return values;
    }

    @Override
    public GisValuesAndNavTree getGisValuesAndNavTree(Collection<Long> attrs, String bbox, CmdbFilter filter, String forOwner) {
        checkGisEnabled();
        NavTreeNode navTreeDomains = getGisNavTree();
        GisValuesAndNavTree res = gisValueRepository.getGeoValuesAndNavTree(attrs, bbox, navTreeDomains);
        return new GisValuesAndNavTreeImpl(filterGisValues(res.getGisValues(), filter, forOwner), filterNavTree(res.getNavTree(), filter, forOwner));
    }

    @Override
    @Nullable
    public Area getAreaForValues(Collection<Long> attrs, CmdbFilter filter, String forOwner) {
        checkGisEnabled();
        return gisValueRepository.getAreaForValues(attrs, filter, forOwner);
    }

    @Override
    @Nullable
    public Area getUserAreaForValues(Collection<Long> attrs, CmdbFilter filter) {
        checkGisEnabled();
        checkArgument(filter.isNoop(), "filter not supported yet");
        gisValueRepository.getOwnerIdGeometryForValues(attrs).stream().filter(e -> dao.getCard(e.getLeft()).getType().hasServiceListPermission()).collect(toList());
        return null;
    }

    @Override
    public GisValue setGisValue(GisValue value) {
        GisAttribute attribute = getGisAttributeIncludeInherited(value.getOwnerClassId(), value.getLayerName());
        checkIsPostgis(attribute);
        gisValueRepository.setGisValue(attribute, cmGeometryToPostgisSql(value.getGeometry()), value.getOwnerCardId());
        return getGisValue(value.getOwnerClassId(), value.getOwnerCardId(), value.getLayerName());
    }

    @Override
    public GisValue setGisValueWithCurrentUser(GisValue value) {
        GisAttribute attribute = getGisAttributeIncludeInherited(value.getOwnerClassId(), value.getLayerName());
        checkIsPostgis(attribute);
        checkArgument(userClassService.getUserClass(attribute.getOwnerClassName()).hasGisAttributeWritePermission(attribute.getLayerName()), format("User doesn't have write permission on the specified GeoAttribute < %s >", attribute.getLayerName()));
        gisValueRepository.setGisValue(attribute, cmGeometryToPostgisSql(value.getGeometry()), value.getOwnerCardId());
        return getGisValueForCurrentUser(value.getOwnerClassId(), value.getOwnerCardId(), value.getLayerName());

    }

    @Override
    public void deleteGisValueWithCurrentUser(String classId, long cardId, String attrId) {
        GisAttribute attribute = getGisAttributeIncludeInherited(classId, attrId);
        checkArgument(userClassService.getUserClass(attribute.getOwnerClassName()).hasGisAttributeWritePermission(attribute.getLayerName()), format("User doesn't have write permission on the specified GeoAttribute < %s >", attribute.getLayerName()));
        checkIsPostgis(attribute);
        gisValueRepository.deleteGisValue(attribute, cardId);
    }

    @Override
    @Transactional
    public void updateGeoAttributesVisibilityForClass(String classId, Collection<Long> newVis) {
        Classe classe = dao.getClasse(classId);
        newVis = set(checkNotNull(newVis));
        Set<Long> currentVis = getGisAttributesVisibleFromClass(classId).stream().map(GisAttribute::getId).collect(toSet());
        Set<Long> toAdd = set(newVis).without(currentVis),
                toRemove = set(currentVis).without(newVis);
        toAdd.forEach((id) -> {
            GisAttribute attr = gisAttributeRepository.getLayer(id);
            attr = GisAttributeImpl.copyOf(attr).withVisibility(set(attr.getVisibility()).with(classe.getName())).build();
            gisAttributeRepository.update(attr);
        });
        toRemove.forEach((id) -> {
            GisAttribute attr = gisAttributeRepository.getLayer(id);
            attr = GisAttributeImpl.copyOf(attr).withVisibility(set(attr.getVisibility()).without(classe.getName())).build();
            gisAttributeRepository.update(attr);
        });
    }

    @Override
    public List<GisAttribute> updateGisAttributesOrder(List<Long> attrIdsInOrder) {
        checkArgument(set(attrIdsInOrder).size() == attrIdsInOrder.size(), "invalid attr id list: list contains duplicates");
        AtomicInteger index = new AtomicInteger(0);
        return attrIdsInOrder.stream().map(gisAttributeRepository::getLayer).map(l -> GisAttributeImpl.copyOf(l).withIndex(index.getAndIncrement()).build()).map(gisAttributeRepository::update).collect(toList());
    }

    @Override
    @Transactional //TODO fix this !!
    public GeoserverLayer setGeoserverLayer(String classId, String attrName, long cardId, DataHandler file) {
        checkGisEnabled();
        checkGeoServerIsEnabled();
        GeoserverLayer geoserverLayer = geoserverLayerRepository.getByCodeOrNull(classId, attrName, cardId);
        if (geoserverLayer == null) {
            String storeName = format("%s_%s_%s", classId, attrName, cardId).toLowerCase();
            geoserverLayer = geoserverLayerRepository.create(GeoserverLayerImpl.builder()//TODO rollback create if there is an error in geoserver upload
                    .withAttribute(getGisAttributeIncludeInherited(classId, attrName))
                    .withOwnerClass(classId)
                    .withOwnerCard(cardId)
                    .withGeoserverStore(storeName)
                    .withGeoserverLayer(storeName)
                    .build());
        }
        geoserverLayer = geoserverService.set(geoserverLayer, toBigByteArray(file));
        GeoserverLayer geoLayer = geoserverLayerRepository.update(geoserverLayer);
        return GeoserverLayerImpl.copyOf(geoLayer).withCenter(geoserverService.getGeoserverLayerCenter(geoLayer.getGeoserverLayer())).build();
    }

    @Override
    public GeoserverLayer updateGeoserverLayer(GeoserverLayer geoserverLayer) {
        checkGisEnabled();
        checkGeoServerIsEnabled();
        GeoserverLayer geoLayer = geoserverLayerRepository.get(geoserverLayer.getId());
        geoLayer = geoserverLayerRepository.update(GeoserverLayerImpl.copyOf(geoLayer).withActive(geoserverLayer.isActive()).build());
        return GeoserverLayerImpl.copyOf(geoLayer).withCenter(geoserverLayer.getCenter()).build();
    }

    @Override
    public GeoserverLayer getGeoserverLayerByCodeOrNull(String classId, String layerCode, long cardId) {
        GeoserverLayer geoLayer = geoserverLayerRepository.getByCodeOrNull(classId, layerCode, cardId);
        if (geoLayer == null) {
            return null;
        } else {
            return GeoserverLayerImpl.copyOf(geoLayer).withCenter(geoserverService.getGeoserverLayerCenter(geoLayer.getGeoserverLayer())).build();
        }
    }

    @Override
    public GeoserverLayer getGeoserverLayerByIdOrNull(String classId, Long layerId, long cardId) {
        GeoserverLayer geoLayer = geoserverLayerRepository.getByIdOrNull(classId, layerId, cardId);
        if (geoLayer == null) {
            return null;
        } else {
            return GeoserverLayerImpl.copyOf(geoLayer).withCenter(geoserverService.getGeoserverLayerCenter(geoLayer.getGeoserverLayer())).build();
        }
    }

    @Override
    @Transactional
    public void deleteGeoServerLayer(long id) {
        checkGisEnabled();
        checkGeoServerIsEnabled();
        GeoserverLayer layer = geoserverLayerRepository.get(id);
        geoserverService.delete(layer);
        geoserverLayerRepository.delete(id);
    }

    @Override
    @Transactional
    public List<GeoserverLayer> getGeoServerLayers() {
        checkGisEnabled();
        return geoserverLayerRepository.getAll().stream().map(l
                -> GeoserverLayerImpl.copyOf(l).withCenter(geoserverService.getGeoserverLayerCenter(l.getGeoserverLayer())).build()).collect(toList());
    }

    @Override
    public List<GeoserverLayer> getGeoServerLayersForCard(String classId, Long cardId) {
        checkGisEnabled();
        return geoserverLayerRepository.getForCard(dao.getClasse(classId), cardId).stream().map(l
                -> GeoserverLayerImpl.copyOf(l).withCenter(geoserverService.getGeoserverLayerCenter(l.getGeoserverLayer())).build()).collect(toList());
    }

    @Override
    public List<GisAttribute> getGisAttributes() {
        checkGisEnabled();
        return gisAttributeRepository.getAllLayers();
    }

    @Override
    public GisAttribute getGisAttributeIncludeInherited(String classId, String attributeId) {
        checkGisEnabled();
        return getGisAttributesByOwnerClassIncludeInherited(classId).stream()
                .filter(a -> equal(a.getLayerName(), attributeId))
                .collect(onlyElement("gis attr not found for owner =< %s > name =< %s >", classId, attributeId));
    }

    @Override
    public GisAttribute getGisAttributeWithCurrentUser(long attributeId) {
        checkGisEnabled();
        GisAttribute gisAttribute = gisAttributeRepository.getLayer(attributeId);
        checkArgument(userClassService.getUserClass(gisAttribute.getOwnerClassName()).hasGisAttributeReadPermission(gisAttribute.getLayerName()), format("User not allowed to access the specified GeoAttribute =< %s >", gisAttribute.getLayerName()));
        return gisAttribute;
    }

    @Override
    public List<GisAttribute> getGisAttributesByOwnerClassIncludeInherited(String classId) {
        checkGisEnabled();
        Classe classe = dao.getClasse(classId);
        return classe.getAncestorsAndSelf().stream().flatMap(c -> gisAttributeRepository.getLayersByOwnerClass(c).stream()).distinct().collect(toImmutableList());
    }

    @Override
    public List<GisAttribute> getGisAttributesVisibleFromClass(String classId) {
        checkGisEnabled();
        return gisAttributeRepository.getVisibleLayersForClass(classId);
    }

    @Override
    public NavTreeNode getGisNavTree() {
        return domainTreeStore.getTree(DOMAIN_TREE_TYPE).getData();
    }

    private List<GisValue> getGisValues(Classe classe, long cardId) {
        checkGisEnabled();
        return getGisAttributesByOwnerClassIncludeInherited(classe.getName()).stream()
                .filter(e -> e.isPostgis() && classe.hasGisAttributeReadPermission(e.getLayerName()))
                .map((l) -> gisValueRepository.getGisValueOrNull(l, cardId)).filter(notNull()).collect(toList());//TODO avoid n*m query        
    }

    private List<GisValue> filterGisValues(List<GisValue> values, CmdbFilter filter, String forOwner) {
        if (filter.isNoop()) {
            return values;
        } else {
            return index(values, GisValue::getOwnerClassId).asMap().entrySet().stream().flatMap(e -> {
                Set<Long> cards;
                if ((!filter.hasAttributeFilter() && !filter.hasRelationFilter()) || equal(e.getKey(), forOwner)) {
                    cards = dao.select(ATTR_ID).from(e.getKey()).where(ATTR_ID, IN, list(e.getValue()).map(GisValue::getOwnerCardId)).where(filter).getCards().stream().map(Card::getId).collect(toImmutableSet());
                } else {
                    cards = dao.select(ATTR_ID).from(e.getKey()).where(ATTR_ID, IN, list(e.getValue()).map(GisValue::getOwnerCardId)).getCards().stream().map(Card::getId).collect(toImmutableSet());
                }
                return e.getValue().stream().filter(v -> cards.contains(v.getOwnerCardId()));
            }).collect(toImmutableList());//TODO improve this, make single query with join
        }
    }

    private List<GisNavTreeNode> filterNavTree(List<GisNavTreeNode> values, CmdbFilter filter, String forOwner) {
        if (filter.isNoop()) {
            return values;
        } else {
            return index(values, GisNavTreeNode::getClassId).asMap().entrySet().stream().flatMap(e -> {
                Set<Long> cards;
                if ((!filter.hasAttributeFilter() && !filter.hasRelationFilter()) || equal(e.getKey(), forOwner)) {
                    cards = dao.select(ATTR_ID).from(e.getKey()).where(ATTR_ID, IN, list(e.getValue()).map(GisNavTreeNode::getCardId)).where(filter).getCards().stream().map(Card::getId).collect(toImmutableSet());
                } else {
                    cards = dao.select(ATTR_ID).from(e.getKey()).where(ATTR_ID, IN, list(e.getValue()).map(GisNavTreeNode::getCardId)).getCards().stream().map(Card::getId).collect(toImmutableSet());
                }
                return e.getValue().stream().filter(v -> cards.contains(v.getCardId()));
            }).collect(toImmutableList());
        }
    }

    private void checkGisEnabled() {
        if (!isGisEnabled()) {
            throw new GisException("GIS Module is non enabled");
        }
    }

    private void checkGeoServerIsEnabled() {
        if (!configuration.isGeoServerEnabled()) {
            throw new GisException("GEOServer is non enabled");
        }
    }

    private void checkIsPostgis(GisAttribute attribute) {
        checkArgument(attribute.isPostgis(), "not a postgis attribute = %s", attribute);
    }
}
