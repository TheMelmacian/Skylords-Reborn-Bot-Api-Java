package eu.skylords.botapi;

import com.fasterxml.jackson.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

public class Types {
    public static class ApiVersion {
        public static final long VERSION = 25;
    }

    private Types() {
        // prevent class initialization
    }

    /** Determine subtypes at runtime */
    public interface MultiType<E extends Enum<E>> {
        E getType();
    }
    public enum Upgrade {
        U0(0),
        U1(1000000),
        /**  Promo must be U2, do not ask me, that is EA's mystery. */
        U2(2000000),
        U3(3000000);

        //----------------------------------------
        public final int value;
        Upgrade(int value) { this.value = value; }
        @JsonValue
        public int getValue() { return value; }
        public static Optional<Upgrade> fromValue(int value) {
            return Arrays.stream(Upgrade.values())
                    .filter(u -> u.value == value)
                    .findFirst();
        }
    }

    /**  ID of the card resource */
    public record CardId(@JsonValue int value) {}

    /**  ID of squad resource */
    public record SquadId(@JsonValue int value) {}

    /**  ID of building resource */
    public record BuildingId(@JsonValue int value) {}

    /**  ID of spell resource */
    public record SpellId(@JsonValue int value) {}

    /**  ID of ability resource */
    public record AbilityId(@JsonValue int value) {}

    /**  ID of mode resource */
    public record ModeId(@JsonValue int value) {}

    /**  ID of an entity present in the match unique to that match
     *  First entity have ID 1, next 2, ...
     *  Ids are never reused
     */
    public record EntityId(@JsonValue int value) {}

    /**  Time information 1 tick = 0.1s = 100 ms */
    public record Tick(@JsonValue int value) {}

    /**  Difference between two `Tick` (points in times, remaining time, ...) */
    public record TickCount(@JsonValue int value) {}

    /**  `x` and `z` are coordinates on the 2D map. */
    public static class Position {
        @JsonProperty(required = true)
        private float x;
        /**  Also known as height. */
        @JsonProperty(required = true)
        private float y;
        @JsonProperty(required = true)
        private float z;
        public float getX() { return x; }
        public void setX(float v) { this.x = v; }
        public float getY() { return y; }
        public void setY(float v) { this.y = v; }
        public float getZ() { return z; }
        public void setZ(float v) { this.z = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position that = (Position) o;
            return getX() == that.getX() && getY() == that.getY() && getZ() == that.getZ();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getX(), getY(), getZ());
        }
        @Override
        public String toString() {
            return "{" + "x: " + x + ", y: " + y + ", z: " + z + "}";
        }
        /**  `x` and `z` are coordinates on the 2D map. */
        public Position() { }
        /**  `x` and `z` are coordinates on the 2D map. */
        public Position(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class Position2D {
        @JsonProperty(required = true)
        private float x;
        @JsonProperty(required = true)
        private float y;
        public float getX() { return x; }
        public void setX(float v) { this.x = v; }
        public float getY() { return y; }
        public void setY(float v) { this.y = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position2D that = (Position2D) o;
            return getX() == that.getX() && getY() == that.getY();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getX(), getY());
        }
        @Override
        public String toString() {
            return "{" + "x: " + x + ", y: " + y + "}";
        }
        public Position2D() { }
        public Position2D(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Position2DWithOrientation {
        @JsonProperty(required = true)
        private float x;
        @JsonProperty(required = true)
        private float y;
        /**  in default camera orientation
         *  0 = down, π/2 = right, π = up, π3/2 = left
         */
        @JsonProperty(required = true)
        private float orientation;
        public float getX() { return x; }
        public void setX(float v) { this.x = v; }
        public float getY() { return y; }
        public void setY(float v) { this.y = v; }
        public float getOrientation() { return orientation; }
        public void setOrientation(float v) { this.orientation = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position2DWithOrientation that = (Position2DWithOrientation) o;
            return getX() == that.getX() && getY() == that.getY() && getOrientation() == that.getOrientation();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getX(), getY(), getOrientation());
        }
        @Override
        public String toString() {
            return "{" + "x: " + x + ", y: " + y + ", orientation: " + orientation + "}";
        }
        public Position2DWithOrientation() { }
        public Position2DWithOrientation(float x, float y, float orientation) {
            this.x = x;
            this.y = y;
            this.orientation = orientation;
        }
    }

    /**  Color of an orb. */
    public enum OrbColor {
        White(0),
        Shadow(1),
        Nature(2),
        Frost(3),
        Fire(4),
        Starting(5),
        All(7);

        //----------------------------------------
        public final int value;
        OrbColor(int value) { this.value = value; }
        @JsonValue
        public int getValue() { return value; }
        public static Optional<Upgrade> fromValue(int value) {
            return Arrays.stream(Upgrade.values())
                    .filter(u -> u.value == value)
                    .findFirst();
        }
    }

    /**  Subset of `OrbColor`, because creating the other colors does not make sense. */
    public enum CreateOrbColor {
        Shadow(1),
        Nature(2),
        Frost(3),
        Fire(4);

        //----------------------------------------
        public final int value;
        CreateOrbColor(int value) { this.value = value; }
        @JsonValue
        public int getValue() { return value; }
        public static Optional<Upgrade> fromValue(int value) {
            return Arrays.stream(Upgrade.values())
                    .filter(u -> u.value == value)
                    .findFirst();
        }
    }

    public enum SingleTargetType {
        SingleEntity,
        Location;
    }
    /** Marker fo all SingleTarget implementations */
    public interface SingleTarget extends MultiType<SingleTargetType> {}
    /**  Target entity */
    public static final class SingleTargetSingleEntity implements  SingleTarget {
        @JsonProperty(required = true)
        private EntityId id;
        @Override
        @JsonIgnore
        public SingleTargetType getType() { return SingleTargetType.SingleEntity; }
        public EntityId getId() { return id; }
        public void setId(EntityId v) { this.id = v; }
        /**  Target entity */
        public SingleTargetSingleEntity() { }
        /**  Target entity */
        public SingleTargetSingleEntity(EntityId id) {
            this.id = id;
        }
    }
    /**  Target location on the ground */
    public static final class SingleTargetLocation implements  SingleTarget {
        @JsonProperty(required = true)
        private Position2D xy;
        @Override
        @JsonIgnore
        public SingleTargetType getType() { return SingleTargetType.Location; }
        public Position2D getXy() { return xy; }
        public void setXy(Position2D v) { this.xy = v; }
        /**  Target location on the ground */
        public SingleTargetLocation() { }
        /**  Target location on the ground */
        public SingleTargetLocation(Position2D xy) {
            this.xy = xy;
        }
    }
    /**  When targeting you can target either entity, or ground coordinates. */
    public static class SingleTargetHolder {
        /**  Target entity */
        @JsonProperty(value = "SingleEntity")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private SingleTargetSingleEntity singleEntity;
        /**  Target location on the ground */
        @JsonProperty(value = "Location")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private SingleTargetLocation location;
        @JsonIgnore
        public SingleTarget get() {
            if (singleEntity != null) {
                return singleEntity;
            }
            else if (location != null) {
                return location;
            }
            else {
                throw new IllegalStateException("SingleTargetHolder doesn't contain any SingleTarget. Check implementation and API!");
            }
        }
        public SingleTargetHolder() { }
        public SingleTargetHolder(SingleTarget v) {
            Objects.requireNonNull(v, "SingleTarget must not be null");
            switch (v.getType()) {
                case SingleTargetType.SingleEntity:
                    this.singleEntity = (SingleTargetSingleEntity) v;
                    break;
                case SingleTargetType.Location:
                    this.location = (SingleTargetLocation) v;
                    break;
                default: throw new IllegalArgumentException("Unknown SingleTarget " + v.getType());
            }
        }
        public SingleTargetSingleEntity getSingleEntity() { return singleEntity; }
        public SingleTargetLocation getLocation() { return location; }
    }

    public enum TargetType {
        Single,
        Multi;
    }
    /** Marker fo all Target implementations */
    public interface Target extends MultiType<TargetType> {}
    public static final class TargetSingle implements  Target {
        @JsonProperty(required = true)
        private SingleTargetHolder single;
        @Override
        @JsonIgnore
        public TargetType getType() { return TargetType.Single; }
        public SingleTargetHolder getSingle() { return single; }
        public void setSingle(SingleTargetHolder v) { this.single = v; }
        public TargetSingle() { }
        public TargetSingle(SingleTargetHolder single) {
            this.single = single;
        }
    }
    public static final class TargetMulti implements  Target {
        @JsonProperty(required = true)
        private Position2D xy_begin;
        @JsonProperty(required = true)
        private Position2D xy_end;
        @Override
        @JsonIgnore
        public TargetType getType() { return TargetType.Multi; }
        public Position2D getXyBegin() { return xy_begin; }
        public void setXyBegin(Position2D v) { this.xy_begin = v; }
        public Position2D getXyEnd() { return xy_end; }
        public void setXyEnd(Position2D v) { this.xy_end = v; }
        public TargetMulti() { }
        public TargetMulti(Position2D xy_begin, Position2D xy_end) {
            this.xy_begin = xy_begin;
            this.xy_end = xy_end;
        }
    }
    public static class TargetHolder {
        @JsonProperty(value = "Single")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private TargetSingle single;
        @JsonProperty(value = "Multi")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private TargetMulti multi;
        @JsonIgnore
        public Target get() {
            if (single != null) {
                return single;
            }
            else if (multi != null) {
                return multi;
            }
            else {
                throw new IllegalStateException("TargetHolder doesn't contain any Target. Check implementation and API!");
            }
        }
        public TargetHolder() { }
        public TargetHolder(Target v) {
            Objects.requireNonNull(v, "Target must not be null");
            switch (v.getType()) {
                case TargetType.Single:
                    this.single = (TargetSingle) v;
                    break;
                case TargetType.Multi:
                    this.multi = (TargetMulti) v;
                    break;
                default: throw new IllegalArgumentException("Unknown Target " + v.getType());
            }
        }
        public TargetSingle getSingle() { return single; }
        public TargetMulti getMulti() { return multi; }
    }

    public enum WalkMode {
        PartialForce(1),
        Force(2),
        /**  Also called by players "Attack move", or "Q move" */
        Normal(4),
        Crusade(5),
        Scout(6),
        Patrol(7);

        //----------------------------------------
        public final int value;
        WalkMode(int value) { this.value = value; }
        @JsonValue
        public int getValue() { return value; }
        public static Optional<Upgrade> fromValue(int value) {
            return Arrays.stream(Upgrade.values())
                    .filter(u -> u.value == value)
                    .findFirst();
        }
    }

    public static class CommunityMapInfo {
        /**  Name of the map. */
        @JsonProperty(required = true)
        private String name;
        /**  Checksum of them map. */
        @JsonProperty(required = true)
        private long crc;
        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public long getCrc() { return crc; }
        public void setCrc(long v) { this.crc = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CommunityMapInfo that = (CommunityMapInfo) o;
            return getName() == that.getName() && getCrc() == that.getCrc();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getName(), getCrc());
        }
        @Override
        public String toString() {
            return "{" + "name: " + name + ", crc: " + crc + "}";
        }
        public CommunityMapInfo() { }
        public CommunityMapInfo(String name, long crc) {
            this.name = name;
            this.crc = crc;
        }
    }

    /**  Official spectator maps are normal maps (have unique id) so only `map` field is needed. */
    public static class MapInfo {
        /**  Represents the map, unfortunately EA decided, it will be harder for community maps. */
        @JsonProperty(required = true)
        private Maps map;
        /**  Is relevant only for community maps. */
        @JsonProperty(required = false)
        private CommunityMapInfo community_map_details;
        public Maps getMap() { return map; }
        public void setMap(Maps v) { this.map = v; }
        public CommunityMapInfo getCommunityMapDetails() { return community_map_details; }
        public void setCommunityMapDetails(CommunityMapInfo v) { this.community_map_details = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MapInfo that = (MapInfo) o;
            return getMap() == that.getMap() && getCommunityMapDetails() == that.getCommunityMapDetails();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getMap(), getCommunityMapDetails());
        }
        @Override
        public String toString() {
            return "{" + "map: " + map + ", community_map_details: " + community_map_details + "}";
        }
        /**  Official spectator maps are normal maps (have unique id) so only `map` field is needed. */
        public MapInfo() { }
        /**  Official spectator maps are normal maps (have unique id) so only `map` field is needed. */
        public MapInfo(Maps map, CommunityMapInfo community_map_details) {
            this.map = map;
            this.community_map_details = community_map_details;
        }
    }

    public static class Deck {
        /**  Name of the deck, must be unique across decks used by bot, but different bots can have same deck names.
         *  Must not contain spaces, to be addable in game.
         */
        @JsonProperty(required = true)
        private String name;
        /**  Index of a card that will be deck icon 0 to 19 inclusive */
        @JsonProperty(required = true)
        private byte cover_card_index;
        /**  List of 20 cards in deck.
         *  Fill empty spaces with `NotACard`.
         */
        @JsonProperty(required = true)
        private CardId[/*size=20*/] cards;
        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public byte getCoverCardIndex() { return cover_card_index; }
        public void setCoverCardIndex(byte v) { this.cover_card_index = v; }
        public CardId[/*size=20*/] getCards() { return cards; }
        public void setCards(CardId[/*size=20*/] v) { this.cards = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Deck that = (Deck) o;
            return getName() == that.getName() && getCoverCardIndex() == that.getCoverCardIndex() && getCards() == that.getCards();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getName(), getCoverCardIndex(), getCards());
        }
        @Override
        public String toString() {
            return "{" + "name: " + name + ", cover_card_index: " + cover_card_index + ", cards: " + cards + "}";
        }
        public Deck() { }
        public Deck(String name, byte cover_card_index, CardId[/*size=20*/] cards) {
            this.name = name;
            this.cover_card_index = cover_card_index;
            this.cards = cards;
        }
    }

    public enum AbilityLine {
        _EAsBug_betterSafeThanSorry(0),
        ModifyWalkSpeed(1),
        UnControllable(4),
        UnKillable(5),
        UnAttackable(9),
        HitMultiple(10),
        _ACModifier(14),
        DamageOverTime(15),
        DamageBuff(21),
        PowerOutputModifier(23),
        MoveSpeedOverwrite(24),
        HitMultipleRanged(25),
        HPModifier(26),
        Aura(27),
        PreventCardPlay(29),
        _SpreadFire(31),
        _SquadSpawnZone(32),
        HitMultipleProjectile(33),
        _MarkedTargetDamageMultiplier(36),
        _MarkedTargetDamage(37),
        _AttackPauseDelay(39),
        _MarkedForTeleport(40),
        _RangeModifier(41),
        ForceAttack(42),
        OnEntityDie(44),
        _FireLanceAbility(47),
        Collector(50),
        _ChangeTargetAggro(51),
        _FireLanceBurstCollector(53),
        RegenerationOld(54),
        _TimedSpell(57),
        Scatter(58),
        _DamageOverTimeNoCombat(59),
        LifeStealer(60),
        BarrierGate(61),
        GlobalRevive(62),
        TrampleResistance(64),
        TrampleOverwrite(65),
        PushbackResistance(66),
        MeleePushbackOverride(67),
        _FanCollector(69),
        MeleeFightSpeedModifier(71),
        _SpellRangeModifierIncoming(72),
        SpellRangeModifierOutgoing(73),
        _FanCollectorBurst(74),
        RangedFightSpeedModifier(75),
        _SquadRestore(76),
        DamagePowerTransfer(79),
        TimedSpell(80),
        TrampleRevengeDamage(81),
        LinkedFire(83),
        DamageBuffAgainst(84),
        IncomingDamageModifier(85),
        GeneratorPower(86),
        IceShield(87),
        DoTRefresh(88),
        EnrageThreshold(89),
        Immunity(90),
        _UnitSpawnZone(91),
        Rage(92),
        _PassiveCharge(93),
        MeleeHitSpell(95),
        _FireDebuff(97),
        FrostDebuff(98),
        SpellBlocker(100),
        ShadowDebuff(102),
        SuicidalBomb(103),
        GrantToken(110),
        TurretCannon(112),
        SpellOnSelfCast(113),
        AbilityOnSelfResolve(114),
        SuppressUserCommand(118),
        LineCast(120),
        NoCheer(132),
        UnitShredderJobCondition(133),
        DamageRadialArea(134),
        _DamageConeArea(137),
        DamageConeCutArea(138),
        ConstructionRepairModifier(139),
        Portal(140),
        Tunnel(141),
        ModeConditionDelay(142),
        HealAreaRadial(144),
        _145LeftoverDoesNotReallyExistButIsUsed(145),
        _146LeftoverDoesNotReallyExistButIsUsed(146),
        OverrideWeaponType(151),
        DamageRadialAreaUsingCorpse(153),
        HealAreaRadialInstantContinues(154),
        ChargeableBombController(155),
        ChargeAttack(156),
        ChargeableBomb(157),
        ModifyRotationSpeed(159),
        ModifyAcceleration(160),
        FormationOverwrite(161),
        EffectHolder(162),
        WhiteRangersHomeDefenseTrigger(163),
        _167LeftoverDoesNotReallyExistButIsUsed(167),
        _168LeftoverDoesNotReallyExistButIsUsed(168),
        HealReservoirUsingCorpse(170),
        ModeChangeBlocker(171),
        BarrierModuleEnterBlock(172),
        ProduceAmmoUsingCorpseInjurity(173),
        IncomingDamageSpreadOnTargetAlignmentArea1(174),
        DamageSelfOnMeleeHit(175),
        HealthCapCurrent(176),
        ConstructionUnCrushable(179),
        ProduceAmmoOverTime(180),
        BarrierSetBuildDelay(181),
        ChannelTimedSpell(183),
        AuraOnEnter(184),
        ParalyzeAbility(185),
        IgnoreSummoningSickness(186),
        BlockRepair(187),
        Corruption(188),
        UnHealable(189),
        Immobile(190),
        ModifyHealing(191),
        IgnoreInCardCondition(192),
        MovementMode(193),
        ConsumeAmmoHealSelf(195),
        ConsumeAmmoHealAreaRadial(196),
        CorpseGather(197),
        AbilityNearEntity(198),
        ModifyIceShieldDecayRate(200),
        ModifyDamageIncomingAuraContingentSelfDamage(201),
        ModifyDamageIncomingAuraContingentSelfDamageTargetAbility(202),
        ConvertCorpseToPower(203),
        EraseOverTime(204),
        FireStreamChannel(205),
        DisableMeleeAttack(206),
        AbilityOnPlayer(207),
        GlobalAbilityOnEntity(208),
        AuraModifyCardCost(209),
        AuraModifyBuildTime(210),
        GlobalRotTimeModifier(211),
        _212LeftoverDoesNotReallyExistButIsUsed(212),
        MindControl(213),
        SpellOnEntityNearby(214),
        AmmoConsumeModifyIncomingDamage(216),
        AmmoConsumeModifyOutgoingDamage(217),
        GlobalSuppressRefund(219),
        DirectRefundOnDie(220),
        OutgoingDamageDependendSpell(221),
        DeathCounter(222),
        DeathCounterController(223),
        DamageRadialAreaUsingGraveyard(224),
        MovingIntervalCast(225),
        BarrierGateDelay(226),
        EffectHolderAmmo(227),
        FightDependentAbility(228),
        GlobalIgnoreCardPlayConditions(229),
        WormMovement(230),
        DamageRectAreaAligned(231),
        GlobalRefundOnEntityDie(232),
        GlobalDamageAbsorption(233),
        GlobalPowerRecovermentModifier(234),
        GlobalDamageAbsorptionTargetAbility(235),
        OverwriteVisRange(236),
        DamageOverTimeCastDepending(237),
        ModifyDamageIncomingAuraContingentSelfRadialAreaDamage(238),
        SuperWeaponShadow(239),
        NoMeleeAgainstAir(240),
        _SuperWeaponShadowDamage(242),
        NoCardPlay(243),
        NoClaim(244),
        DamageRadialAreaAmmo(246),
        PathLayerOverride(247),
        ChannelBlock(248),
        Polymorph(249),
        Delay(250),
        ModifyDamageIncomingOnFigure(251),
        ImmobileRoot(252),
        GlobalModifyCorpseGather(253),
        AbilityDependentAbility(254),
        CorpseManager(255),
        DisableToken(256),
        Piercing(258),
        ReceiveMeleeAttacks(259),
        BuildBlock(260),
        PreventCardPlayAuraBuilding(262),
        GraveyardDependentRecast(263),
        ClaimBlock(264),
        AmmoStartup(265),
        DamageDistribution(266),
        SwapSquadNightGuard(267),
        Revive(268),
        Amok(269),
        NoCombat(270),
        SlowDownDisabled(271),
        CrowdControlTimeModifier(272),
        DamageOnMeleeHit(273),
        IgnoreIncomingDamageModifier(275),
        BlockRevive(278),
        GlobalMorphState(279),
        SpecialOnTarget(280),
        FleshBenderBugSwitch(281),
        TimedMorph(282),
        GlobalBuildTimeModifier(283),
        CardBlock(285),
        IceShieldRegeneration(286),
        HealOverTime(287),
        IceShieldTimerOffset(288),
        SpellOnVanish(289),
        GlobalVoidAbsorption(290),
        VoidContainer(291),
        ConvertCorpseToHealing(292),
        OnEntitySpawn(293),
        OnMorph(294),
        Sprint(295);

        //----------------------------------------
        public final int value;
        AbilityLine(int value) { this.value = value; }
        @JsonValue
        public int getValue() { return value; }
        public static Optional<Upgrade> fromValue(int value) {
            return Arrays.stream(Upgrade.values())
                    .filter(u -> u.value == value)
                    .findFirst();
        }
    }

    public enum AreaShapeType {
        Circle,
        Cone,
        ConeCut,
        WideLine;
    }
    /** Marker fo all AreaShape implementations */
    public interface AreaShape extends MultiType<AreaShapeType> {}
    public static final class AreaShapeCircle implements  AreaShape {
        @JsonProperty(required = true)
        private Position2D center;
        @JsonProperty(required = true)
        private float radius;
        @Override
        @JsonIgnore
        public AreaShapeType getType() { return AreaShapeType.Circle; }
        public Position2D getCenter() { return center; }
        public void setCenter(Position2D v) { this.center = v; }
        public float getRadius() { return radius; }
        public void setRadius(float v) { this.radius = v; }
        public AreaShapeCircle() { }
        public AreaShapeCircle(Position2D center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }
    public static final class AreaShapeCone implements  AreaShape {
        @JsonProperty(required = true)
        private Position2D base;
        @JsonProperty(required = true)
        private float radius;
        @JsonProperty(required = true)
        private float angle;
        @Override
        @JsonIgnore
        public AreaShapeType getType() { return AreaShapeType.Cone; }
        public Position2D getBase() { return base; }
        public void setBase(Position2D v) { this.base = v; }
        public float getRadius() { return radius; }
        public void setRadius(float v) { this.radius = v; }
        public float getAngle() { return angle; }
        public void setAngle(float v) { this.angle = v; }
        public AreaShapeCone() { }
        public AreaShapeCone(Position2D base, float radius, float angle) {
            this.base = base;
            this.radius = radius;
            this.angle = angle;
        }
    }
    public static final class AreaShapeConeCut implements  AreaShape {
        @JsonProperty(required = true)
        private Position2D start;
        @JsonProperty(required = true)
        private Position2D end;
        @JsonProperty(required = true)
        private float radius;
        @JsonProperty(required = true)
        private float width_near;
        @JsonProperty(required = true)
        private float width_far;
        @Override
        @JsonIgnore
        public AreaShapeType getType() { return AreaShapeType.ConeCut; }
        public Position2D getStart() { return start; }
        public void setStart(Position2D v) { this.start = v; }
        public Position2D getEnd() { return end; }
        public void setEnd(Position2D v) { this.end = v; }
        public float getRadius() { return radius; }
        public void setRadius(float v) { this.radius = v; }
        public float getWidthNear() { return width_near; }
        public void setWidthNear(float v) { this.width_near = v; }
        public float getWidthFar() { return width_far; }
        public void setWidthFar(float v) { this.width_far = v; }
        public AreaShapeConeCut() { }
        public AreaShapeConeCut(Position2D start, Position2D end, float radius, float width_near, float width_far) {
            this.start = start;
            this.end = end;
            this.radius = radius;
            this.width_near = width_near;
            this.width_far = width_far;
        }
    }
    public static final class AreaShapeWideLine implements  AreaShape {
        @JsonProperty(required = true)
        private Position2D start;
        @JsonProperty(required = true)
        private Position2D end;
        @JsonProperty(required = true)
        private float width;
        @Override
        @JsonIgnore
        public AreaShapeType getType() { return AreaShapeType.WideLine; }
        public Position2D getStart() { return start; }
        public void setStart(Position2D v) { this.start = v; }
        public Position2D getEnd() { return end; }
        public void setEnd(Position2D v) { this.end = v; }
        public float getWidth() { return width; }
        public void setWidth(float v) { this.width = v; }
        public AreaShapeWideLine() { }
        public AreaShapeWideLine(Position2D start, Position2D end, float width) {
            this.start = start;
            this.end = end;
            this.width = width;
        }
    }
    public static class AreaShapeHolder {
        @JsonProperty(value = "Circle")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AreaShapeCircle circle;
        @JsonProperty(value = "Cone")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AreaShapeCone cone;
        @JsonProperty(value = "ConeCut")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AreaShapeConeCut coneCut;
        @JsonProperty(value = "WideLine")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AreaShapeWideLine wideLine;
        @JsonIgnore
        public AreaShape get() {
            if (circle != null) {
                return circle;
            }
            else if (cone != null) {
                return cone;
            }
            else if (coneCut != null) {
                return coneCut;
            }
            else if (wideLine != null) {
                return wideLine;
            }
            else {
                throw new IllegalStateException("AreaShapeHolder doesn't contain any AreaShape. Check implementation and API!");
            }
        }
        public AreaShapeHolder() { }
        public AreaShapeHolder(AreaShape v) {
            Objects.requireNonNull(v, "AreaShape must not be null");
            switch (v.getType()) {
                case AreaShapeType.Circle:
                    this.circle = (AreaShapeCircle) v;
                    break;
                case AreaShapeType.Cone:
                    this.cone = (AreaShapeCone) v;
                    break;
                case AreaShapeType.ConeCut:
                    this.coneCut = (AreaShapeConeCut) v;
                    break;
                case AreaShapeType.WideLine:
                    this.wideLine = (AreaShapeWideLine) v;
                    break;
                default: throw new IllegalArgumentException("Unknown AreaShape " + v.getType());
            }
        }
        public AreaShapeCircle getCircle() { return circle; }
        public AreaShapeCone getCone() { return cone; }
        public AreaShapeConeCut getConeCut() { return coneCut; }
        public AreaShapeWideLine getWideLine() { return wideLine; }
    }

    public enum AbilityEffectSpecificType {
        DamageArea,
        DamageOverTime,
        LinkedFire,
        SpellOnEntityNearby,
        TimedSpell,
        Collector,
        Aura,
        MovingIntervalCast,
        Other;
    }
    /** Marker fo all AbilityEffectSpecific implementations */
    public interface AbilityEffectSpecific extends MultiType<AbilityEffectSpecificType> {}
    public static final class AbilityEffectSpecificDamageArea implements  AbilityEffectSpecific {
        @JsonProperty(required = true)
        private float progress_current;
        @JsonProperty(required = true)
        private float progress_delta;
        @JsonProperty(required = true)
        private float damage_remaining;
        @JsonProperty(required = true)
        private AreaShapeHolder shape;
        @Override
        @JsonIgnore
        public AbilityEffectSpecificType getType() { return AbilityEffectSpecificType.DamageArea; }
        public float getProgressCurrent() { return progress_current; }
        public void setProgressCurrent(float v) { this.progress_current = v; }
        public float getProgressDelta() { return progress_delta; }
        public void setProgressDelta(float v) { this.progress_delta = v; }
        public float getDamageRemaining() { return damage_remaining; }
        public void setDamageRemaining(float v) { this.damage_remaining = v; }
        public AreaShapeHolder getShape() { return shape; }
        public void setShape(AreaShapeHolder v) { this.shape = v; }
        public AbilityEffectSpecificDamageArea() { }
        public AbilityEffectSpecificDamageArea(float progress_current, float progress_delta, float damage_remaining, AreaShapeHolder shape) {
            this.progress_current = progress_current;
            this.progress_delta = progress_delta;
            this.damage_remaining = damage_remaining;
            this.shape = shape;
        }
    }
    public static final class AbilityEffectSpecificDamageOverTime implements  AbilityEffectSpecific {
        @JsonProperty(required = true)
        private TickCount tick_wait_duration;
        @JsonProperty(required = true)
        private TickCount ticks_left;
        @JsonProperty(required = true)
        private float tick_damage;
        @Override
        @JsonIgnore
        public AbilityEffectSpecificType getType() { return AbilityEffectSpecificType.DamageOverTime; }
        public TickCount getTickWaitDuration() { return tick_wait_duration; }
        public void setTickWaitDuration(TickCount v) { this.tick_wait_duration = v; }
        public TickCount getTicksLeft() { return ticks_left; }
        public void setTicksLeft(TickCount v) { this.ticks_left = v; }
        public float getTickDamage() { return tick_damage; }
        public void setTickDamage(float v) { this.tick_damage = v; }
        public AbilityEffectSpecificDamageOverTime() { }
        public AbilityEffectSpecificDamageOverTime(TickCount tick_wait_duration, TickCount ticks_left, float tick_damage) {
            this.tick_wait_duration = tick_wait_duration;
            this.ticks_left = ticks_left;
            this.tick_damage = tick_damage;
        }
    }
    public static final class AbilityEffectSpecificLinkedFire implements  AbilityEffectSpecific {
        @JsonProperty(required = true)
        private boolean linked;
        @JsonProperty(required = true)
        private boolean fighting;
        @JsonProperty(required = true)
        private int fast_cast;
        @JsonProperty(required = true)
        private short support_cap;
        @JsonProperty(required = true)
        private byte support_production;
        @Override
        @JsonIgnore
        public AbilityEffectSpecificType getType() { return AbilityEffectSpecificType.LinkedFire; }
        public boolean getLinked() { return linked; }
        public void setLinked(boolean v) { this.linked = v; }
        public boolean getFighting() { return fighting; }
        public void setFighting(boolean v) { this.fighting = v; }
        public int getFastCast() { return fast_cast; }
        public void setFastCast(int v) { this.fast_cast = v; }
        public short getSupportCap() { return support_cap; }
        public void setSupportCap(short v) { this.support_cap = v; }
        public byte getSupportProduction() { return support_production; }
        public void setSupportProduction(byte v) { this.support_production = v; }
        public AbilityEffectSpecificLinkedFire() { }
        public AbilityEffectSpecificLinkedFire(boolean linked, boolean fighting, int fast_cast, short support_cap, byte support_production) {
            this.linked = linked;
            this.fighting = fighting;
            this.fast_cast = fast_cast;
            this.support_cap = support_cap;
            this.support_production = support_production;
        }
    }
    public static final class AbilityEffectSpecificSpellOnEntityNearby implements  AbilityEffectSpecific {
        @JsonProperty(required = true)
        private SpellId[] spell_on_owner;
        @JsonProperty(required = true)
        private SpellId[] spell_on_source;
        @JsonProperty(required = true)
        private float radius;
        @JsonProperty(required = true)
        private int remaining_targets;
        @Override
        @JsonIgnore
        public AbilityEffectSpecificType getType() { return AbilityEffectSpecificType.SpellOnEntityNearby; }
        public SpellId[] getSpellOnOwner() { return spell_on_owner; }
        public void setSpellOnOwner(SpellId[] v) { this.spell_on_owner = v; }
        public SpellId[] getSpellOnSource() { return spell_on_source; }
        public void setSpellOnSource(SpellId[] v) { this.spell_on_source = v; }
        public float getRadius() { return radius; }
        public void setRadius(float v) { this.radius = v; }
        public int getRemainingTargets() { return remaining_targets; }
        public void setRemainingTargets(int v) { this.remaining_targets = v; }
        public AbilityEffectSpecificSpellOnEntityNearby() { }
        public AbilityEffectSpecificSpellOnEntityNearby(SpellId[] spell_on_owner, SpellId[] spell_on_source, float radius, int remaining_targets) {
            this.spell_on_owner = spell_on_owner;
            this.spell_on_source = spell_on_source;
            this.radius = radius;
            this.remaining_targets = remaining_targets;
        }
    }
    public static final class AbilityEffectSpecificTimedSpell implements  AbilityEffectSpecific {
        @JsonProperty(required = true)
        private SpellId[] spells_to_cast;
        @Override
        @JsonIgnore
        public AbilityEffectSpecificType getType() { return AbilityEffectSpecificType.TimedSpell; }
        public SpellId[] getSpellsToCast() { return spells_to_cast; }
        public void setSpellsToCast(SpellId[] v) { this.spells_to_cast = v; }
        public AbilityEffectSpecificTimedSpell() { }
        public AbilityEffectSpecificTimedSpell(SpellId[] spells_to_cast) {
            this.spells_to_cast = spells_to_cast;
        }
    }
    public static final class AbilityEffectSpecificCollector implements  AbilityEffectSpecific {
        @JsonProperty(required = true)
        private SpellId spell_to_cast;
        @JsonProperty(required = true)
        private float radius;
        @Override
        @JsonIgnore
        public AbilityEffectSpecificType getType() { return AbilityEffectSpecificType.Collector; }
        public SpellId getSpellToCast() { return spell_to_cast; }
        public void setSpellToCast(SpellId v) { this.spell_to_cast = v; }
        public float getRadius() { return radius; }
        public void setRadius(float v) { this.radius = v; }
        public AbilityEffectSpecificCollector() { }
        public AbilityEffectSpecificCollector(SpellId spell_to_cast, float radius) {
            this.spell_to_cast = spell_to_cast;
            this.radius = radius;
        }
    }
    public static final class AbilityEffectSpecificAura implements  AbilityEffectSpecific {
        @JsonProperty(required = true)
        private SpellId[] spells_to_apply;
        @JsonProperty(required = true)
        private AbilityId[] abilities_to_apply;
        @JsonProperty(required = true)
        private float radius;
        @Override
        @JsonIgnore
        public AbilityEffectSpecificType getType() { return AbilityEffectSpecificType.Aura; }
        public SpellId[] getSpellsToApply() { return spells_to_apply; }
        public void setSpellsToApply(SpellId[] v) { this.spells_to_apply = v; }
        public AbilityId[] getAbilitiesToApply() { return abilities_to_apply; }
        public void setAbilitiesToApply(AbilityId[] v) { this.abilities_to_apply = v; }
        public float getRadius() { return radius; }
        public void setRadius(float v) { this.radius = v; }
        public AbilityEffectSpecificAura() { }
        public AbilityEffectSpecificAura(SpellId[] spells_to_apply, AbilityId[] abilities_to_apply, float radius) {
            this.spells_to_apply = spells_to_apply;
            this.abilities_to_apply = abilities_to_apply;
            this.radius = radius;
        }
    }
    public static final class AbilityEffectSpecificMovingIntervalCast implements  AbilityEffectSpecific {
        @JsonProperty(required = true)
        private SpellId[] spell_to_cast;
        @JsonProperty(required = true)
        private Position2D direction_step;
        @JsonProperty(required = true)
        private TickCount cast_every_nth_tick;
        @Override
        @JsonIgnore
        public AbilityEffectSpecificType getType() { return AbilityEffectSpecificType.MovingIntervalCast; }
        public SpellId[] getSpellToCast() { return spell_to_cast; }
        public void setSpellToCast(SpellId[] v) { this.spell_to_cast = v; }
        public Position2D getDirectionStep() { return direction_step; }
        public void setDirectionStep(Position2D v) { this.direction_step = v; }
        public TickCount getCastEveryNthTick() { return cast_every_nth_tick; }
        public void setCastEveryNthTick(TickCount v) { this.cast_every_nth_tick = v; }
        public AbilityEffectSpecificMovingIntervalCast() { }
        public AbilityEffectSpecificMovingIntervalCast(SpellId[] spell_to_cast, Position2D direction_step, TickCount cast_every_nth_tick) {
            this.spell_to_cast = spell_to_cast;
            this.direction_step = direction_step;
            this.cast_every_nth_tick = cast_every_nth_tick;
        }
    }
    /**  If you think something interesting got hidden by Other report it */
    public static final class AbilityEffectSpecificOther implements  AbilityEffectSpecific {
        @Override
        @JsonIgnore
        public AbilityEffectSpecificType getType() { return AbilityEffectSpecificType.Other; }
        /**  If you think something interesting got hidden by Other report it */
        public AbilityEffectSpecificOther() { }
    }
    public static class AbilityEffectSpecificHolder {
        @JsonProperty(value = "DamageArea")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificDamageArea damageArea;
        @JsonProperty(value = "DamageOverTime")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificDamageOverTime damageOverTime;
        @JsonProperty(value = "LinkedFire")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificLinkedFire linkedFire;
        @JsonProperty(value = "SpellOnEntityNearby")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificSpellOnEntityNearby spellOnEntityNearby;
        @JsonProperty(value = "TimedSpell")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificTimedSpell timedSpell;
        @JsonProperty(value = "Collector")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificCollector collector;
        @JsonProperty(value = "Aura")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificAura aura;
        @JsonProperty(value = "MovingIntervalCast")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificMovingIntervalCast movingIntervalCast;
        /**  If you think something interesting got hidden by Other report it */
        @JsonProperty(value = "Other")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificOther other;
        @JsonIgnore
        public AbilityEffectSpecific get() {
            if (damageArea != null) {
                return damageArea;
            }
            else if (damageOverTime != null) {
                return damageOverTime;
            }
            else if (linkedFire != null) {
                return linkedFire;
            }
            else if (spellOnEntityNearby != null) {
                return spellOnEntityNearby;
            }
            else if (timedSpell != null) {
                return timedSpell;
            }
            else if (collector != null) {
                return collector;
            }
            else if (aura != null) {
                return aura;
            }
            else if (movingIntervalCast != null) {
                return movingIntervalCast;
            }
            else if (other != null) {
                return other;
            }
            else {
                throw new IllegalStateException("AbilityEffectSpecificHolder doesn't contain any AbilityEffectSpecific. Check implementation and API!");
            }
        }
        public AbilityEffectSpecificHolder() { }
        public AbilityEffectSpecificHolder(AbilityEffectSpecific v) {
            Objects.requireNonNull(v, "AbilityEffectSpecific must not be null");
            switch (v.getType()) {
                case AbilityEffectSpecificType.DamageArea:
                    this.damageArea = (AbilityEffectSpecificDamageArea) v;
                    break;
                case AbilityEffectSpecificType.DamageOverTime:
                    this.damageOverTime = (AbilityEffectSpecificDamageOverTime) v;
                    break;
                case AbilityEffectSpecificType.LinkedFire:
                    this.linkedFire = (AbilityEffectSpecificLinkedFire) v;
                    break;
                case AbilityEffectSpecificType.SpellOnEntityNearby:
                    this.spellOnEntityNearby = (AbilityEffectSpecificSpellOnEntityNearby) v;
                    break;
                case AbilityEffectSpecificType.TimedSpell:
                    this.timedSpell = (AbilityEffectSpecificTimedSpell) v;
                    break;
                case AbilityEffectSpecificType.Collector:
                    this.collector = (AbilityEffectSpecificCollector) v;
                    break;
                case AbilityEffectSpecificType.Aura:
                    this.aura = (AbilityEffectSpecificAura) v;
                    break;
                case AbilityEffectSpecificType.MovingIntervalCast:
                    this.movingIntervalCast = (AbilityEffectSpecificMovingIntervalCast) v;
                    break;
                case AbilityEffectSpecificType.Other:
                    this.other = (AbilityEffectSpecificOther) v;
                    break;
                default: throw new IllegalArgumentException("Unknown AbilityEffectSpecific " + v.getType());
            }
        }
        public AbilityEffectSpecificDamageArea getDamageArea() { return damageArea; }
        public AbilityEffectSpecificDamageOverTime getDamageOverTime() { return damageOverTime; }
        public AbilityEffectSpecificLinkedFire getLinkedFire() { return linkedFire; }
        public AbilityEffectSpecificSpellOnEntityNearby getSpellOnEntityNearby() { return spellOnEntityNearby; }
        public AbilityEffectSpecificTimedSpell getTimedSpell() { return timedSpell; }
        public AbilityEffectSpecificCollector getCollector() { return collector; }
        public AbilityEffectSpecificAura getAura() { return aura; }
        public AbilityEffectSpecificMovingIntervalCast getMovingIntervalCast() { return movingIntervalCast; }
        public AbilityEffectSpecificOther getOther() { return other; }
    }

    public static class AbilityEffect {
        @JsonProperty(required = true)
        private AbilityId id;
        @JsonProperty(required = true)
        private AbilityLine line;
        @JsonProperty(required = true)
        private EntityId source;
        @JsonProperty(required = true)
        private byte source_team;
        @JsonProperty(required = false)
        private Tick start_tick;
        @JsonProperty(required = false)
        private Tick end_tick;
        @JsonProperty(required = true)
        private AbilityEffectSpecificHolder specific;
        public AbilityId getId() { return id; }
        public void setId(AbilityId v) { this.id = v; }
        public AbilityLine getLine() { return line; }
        public void setLine(AbilityLine v) { this.line = v; }
        public EntityId getSource() { return source; }
        public void setSource(EntityId v) { this.source = v; }
        public byte getSourceTeam() { return source_team; }
        public void setSourceTeam(byte v) { this.source_team = v; }
        public Tick getStartTick() { return start_tick; }
        public void setStartTick(Tick v) { this.start_tick = v; }
        public Tick getEndTick() { return end_tick; }
        public void setEndTick(Tick v) { this.end_tick = v; }
        public AbilityEffectSpecificHolder getSpecific() { return specific; }
        public void setSpecific(AbilityEffectSpecificHolder v) { this.specific = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AbilityEffect that = (AbilityEffect) o;
            return getId() == that.getId() && getLine() == that.getLine() && getSource() == that.getSource() && getSourceTeam() == that.getSourceTeam() && getStartTick() == that.getStartTick() && getEndTick() == that.getEndTick() && getSpecific() == that.getSpecific();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getId(), getLine(), getSource(), getSourceTeam(), getStartTick(), getEndTick(), getSpecific());
        }
        @Override
        public String toString() {
            return "{" + "id: " + id + ", line: " + line + ", source: " + source + ", source_team: " + source_team + ", start_tick: " + start_tick + ", end_tick: " + end_tick + ", specific: " + specific + "}";
        }
        public AbilityEffect() { }
        public AbilityEffect(AbilityId id, AbilityLine line, EntityId source, byte source_team, Tick start_tick, Tick end_tick, AbilityEffectSpecificHolder specific) {
            this.id = id;
            this.line = line;
            this.source = source;
            this.source_team = source_team;
            this.start_tick = start_tick;
            this.end_tick = end_tick;
            this.specific = specific;
        }
    }

    public enum MountStateType {
        Unmounted,
        MountingSquad,
        MountingFigure,
        MountedSquad,
        MountedFigure,
        Unknown;
    }
    /** Marker fo all MountState implementations */
    public interface MountState extends MultiType<MountStateType> {}
    /**  not mounted on any barrier (EA's 0) */
    public static final class MountStateUnmounted implements  MountState {
        @Override
        @JsonIgnore
        public MountStateType getType() { return MountStateType.Unmounted; }
        /**  not mounted on any barrier (EA's 0) */
        public MountStateUnmounted() { }
    }
    /**  squad in process of mounting to barrier (EA's 1, 2, 3) */
    public static final class MountStateMountingSquad implements  MountState {
        @JsonProperty(required = true)
        private EntityId barrier;
        @Override
        @JsonIgnore
        public MountStateType getType() { return MountStateType.MountingSquad; }
        public EntityId getBarrier() { return barrier; }
        public void setBarrier(EntityId v) { this.barrier = v; }
        /**  squad in process of mounting to barrier (EA's 1, 2, 3) */
        public MountStateMountingSquad() { }
        /**  squad in process of mounting to barrier (EA's 1, 2, 3) */
        public MountStateMountingSquad(EntityId barrier) {
            this.barrier = barrier;
        }
    }
    /**  figure in process of mounting to barrier (EA's 1, 2, 3) */
    public static final class MountStateMountingFigure implements  MountState {
        @JsonProperty(required = true)
        private EntityId barrier;
        @JsonProperty(required = true)
        private byte slot;
        @Override
        @JsonIgnore
        public MountStateType getType() { return MountStateType.MountingFigure; }
        public EntityId getBarrier() { return barrier; }
        public void setBarrier(EntityId v) { this.barrier = v; }
        public byte getSlot() { return slot; }
        public void setSlot(byte v) { this.slot = v; }
        /**  figure in process of mounting to barrier (EA's 1, 2, 3) */
        public MountStateMountingFigure() { }
        /**  figure in process of mounting to barrier (EA's 1, 2, 3) */
        public MountStateMountingFigure(EntityId barrier, byte slot) {
            this.barrier = barrier;
            this.slot = slot;
        }
    }
    /**  squad mounted to barrier (EA's 4) */
    public static final class MountStateMountedSquad implements  MountState {
        @JsonProperty(required = true)
        private EntityId barrier;
        @Override
        @JsonIgnore
        public MountStateType getType() { return MountStateType.MountedSquad; }
        public EntityId getBarrier() { return barrier; }
        public void setBarrier(EntityId v) { this.barrier = v; }
        /**  squad mounted to barrier (EA's 4) */
        public MountStateMountedSquad() { }
        /**  squad mounted to barrier (EA's 4) */
        public MountStateMountedSquad(EntityId barrier) {
            this.barrier = barrier;
        }
    }
    /**  figure mounted to barrier (EA's 4) */
    public static final class MountStateMountedFigure implements  MountState {
        @JsonProperty(required = true)
        private EntityId barrier;
        @JsonProperty(required = true)
        private byte slot;
        @Override
        @JsonIgnore
        public MountStateType getType() { return MountStateType.MountedFigure; }
        public EntityId getBarrier() { return barrier; }
        public void setBarrier(EntityId v) { this.barrier = v; }
        public byte getSlot() { return slot; }
        public void setSlot(byte v) { this.slot = v; }
        /**  figure mounted to barrier (EA's 4) */
        public MountStateMountedFigure() { }
        /**  figure mounted to barrier (EA's 4) */
        public MountStateMountedFigure(EntityId barrier, byte slot) {
            this.barrier = barrier;
            this.slot = slot;
        }
    }
    /**  Unknown (EA's 5, 6) please report a bug (ideally with steps to reproduce) */
    public static final class MountStateUnknown implements  MountState {
        @JsonProperty(required = true)
        private byte mount_state;
        @JsonProperty(required = true)
        private int enter_exit_barrier_module;
        @JsonProperty(required = true)
        private int target_barrier_module;
        @JsonProperty(required = true)
        private int current_barrier_module;
        @JsonProperty(required = true)
        private int slot;
        @Override
        @JsonIgnore
        public MountStateType getType() { return MountStateType.Unknown; }
        public byte getMountState() { return mount_state; }
        public void setMountState(byte v) { this.mount_state = v; }
        public int getEnterExitBarrierModule() { return enter_exit_barrier_module; }
        public void setEnterExitBarrierModule(int v) { this.enter_exit_barrier_module = v; }
        public int getTargetBarrierModule() { return target_barrier_module; }
        public void setTargetBarrierModule(int v) { this.target_barrier_module = v; }
        public int getCurrentBarrierModule() { return current_barrier_module; }
        public void setCurrentBarrierModule(int v) { this.current_barrier_module = v; }
        public int getSlot() { return slot; }
        public void setSlot(int v) { this.slot = v; }
        /**  Unknown (EA's 5, 6) please report a bug (ideally with steps to reproduce) */
        public MountStateUnknown() { }
        /**  Unknown (EA's 5, 6) please report a bug (ideally with steps to reproduce) */
        public MountStateUnknown(byte mount_state, int enter_exit_barrier_module, int target_barrier_module, int current_barrier_module, int slot) {
            this.mount_state = mount_state;
            this.enter_exit_barrier_module = enter_exit_barrier_module;
            this.target_barrier_module = target_barrier_module;
            this.current_barrier_module = current_barrier_module;
            this.slot = slot;
        }
    }
    /**  State of entity being mounted (or not) on barrier */
    public static class MountStateHolder {
        /**  not mounted on any barrier (EA's 0) */
        @JsonProperty(value = "Unmounted")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private MountStateUnmounted unmounted;
        /**  squad in process of mounting to barrier (EA's 1, 2, 3) */
        @JsonProperty(value = "MountingSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private MountStateMountingSquad mountingSquad;
        /**  figure in process of mounting to barrier (EA's 1, 2, 3) */
        @JsonProperty(value = "MountingFigure")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private MountStateMountingFigure mountingFigure;
        /**  squad mounted to barrier (EA's 4) */
        @JsonProperty(value = "MountedSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private MountStateMountedSquad mountedSquad;
        /**  figure mounted to barrier (EA's 4) */
        @JsonProperty(value = "MountedFigure")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private MountStateMountedFigure mountedFigure;
        /**  Unknown (EA's 5, 6) please report a bug (ideally with steps to reproduce) */
        @JsonProperty(value = "Unknown")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private MountStateUnknown unknown;
        @JsonIgnore
        public MountState get() {
            if (unmounted != null) {
                return unmounted;
            }
            else if (mountingSquad != null) {
                return mountingSquad;
            }
            else if (mountingFigure != null) {
                return mountingFigure;
            }
            else if (mountedSquad != null) {
                return mountedSquad;
            }
            else if (mountedFigure != null) {
                return mountedFigure;
            }
            else if (unknown != null) {
                return unknown;
            }
            else {
                throw new IllegalStateException("MountStateHolder doesn't contain any MountState. Check implementation and API!");
            }
        }
        public MountStateHolder() { }
        public MountStateHolder(MountState v) {
            Objects.requireNonNull(v, "MountState must not be null");
            switch (v.getType()) {
                case MountStateType.Unmounted:
                    this.unmounted = (MountStateUnmounted) v;
                    break;
                case MountStateType.MountingSquad:
                    this.mountingSquad = (MountStateMountingSquad) v;
                    break;
                case MountStateType.MountingFigure:
                    this.mountingFigure = (MountStateMountingFigure) v;
                    break;
                case MountStateType.MountedSquad:
                    this.mountedSquad = (MountStateMountedSquad) v;
                    break;
                case MountStateType.MountedFigure:
                    this.mountedFigure = (MountStateMountedFigure) v;
                    break;
                case MountStateType.Unknown:
                    this.unknown = (MountStateUnknown) v;
                    break;
                default: throw new IllegalArgumentException("Unknown MountState " + v.getType());
            }
        }
        public MountStateUnmounted getUnmounted() { return unmounted; }
        public MountStateMountingSquad getMountingSquad() { return mountingSquad; }
        public MountStateMountingFigure getMountingFigure() { return mountingFigure; }
        public MountStateMountedSquad getMountedSquad() { return mountedSquad; }
        public MountStateMountedFigure getMountedFigure() { return mountedFigure; }
        public MountStateUnknown getUnknown() { return unknown; }
    }

    public enum AspectType {
        PowerProduction,
        Health,
        Combat,
        ModeChange,
        Ammunition,
        SuperWeaponShadow,
        WormMovement,
        NPCTag,
        PlayerKit,
        Loot,
        Immunity,
        Turret,
        Tunnel,
        MountBarrier,
        SpellMemory,
        Portal,
        Hate,
        BarrierGate,
        Attackable,
        SquadRefill,
        PortalExit,
        ConstructionData,
        SuperWeaponShadowBomb,
        RepairBarrierSet,
        ConstructionRepair,
        Follower,
        CollisionBase,
        EditorUniqueID,
        Roam;
    }
    /** Marker fo all Aspect implementations */
    public interface Aspect extends MultiType<AspectType> {}
    /**  Used by *mostly* power wells */
    public static final class AspectPowerProduction implements  Aspect {
        @JsonProperty(required = true)
        private float current_power;
        @JsonProperty(required = true)
        private float power_capacity;
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.PowerProduction; }
        public float getCurrentPower() { return current_power; }
        public void setCurrentPower(float v) { this.current_power = v; }
        public float getPowerCapacity() { return power_capacity; }
        public void setPowerCapacity(float v) { this.power_capacity = v; }
        /**  Used by *mostly* power wells */
        public AspectPowerProduction() { }
        /**  Used by *mostly* power wells */
        public AspectPowerProduction(float current_power, float power_capacity) {
            this.current_power = current_power;
            this.power_capacity = power_capacity;
        }
    }
    /**  Health of an entity. */
    public static final class AspectHealth implements  Aspect {
        @JsonProperty(required = true)
        private float current_hp;
        @JsonProperty(required = true)
        private float cap_current_max;
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Health; }
        public float getCurrentHp() { return current_hp; }
        public void setCurrentHp(float v) { this.current_hp = v; }
        public float getCapCurrentMax() { return cap_current_max; }
        public void setCapCurrentMax(float v) { this.cap_current_max = v; }
        /**  Health of an entity. */
        public AspectHealth() { }
        /**  Health of an entity. */
        public AspectHealth(float current_hp, float cap_current_max) {
            this.current_hp = current_hp;
            this.cap_current_max = cap_current_max;
        }
    }
    public static final class AspectCombat implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Combat; }
        public AspectCombat() { }
    }
    public static final class AspectModeChange implements  Aspect {
        @JsonProperty(required = true)
        private ModeId current_mode;
        @JsonProperty(required = true)
        private ModeId[] all_modes;
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.ModeChange; }
        public ModeId getCurrentMode() { return current_mode; }
        public void setCurrentMode(ModeId v) { this.current_mode = v; }
        public ModeId[] getAllModes() { return all_modes; }
        public void setAllModes(ModeId[] v) { this.all_modes = v; }
        public AspectModeChange() { }
        public AspectModeChange(ModeId current_mode, ModeId[] all_modes) {
            this.current_mode = current_mode;
            this.all_modes = all_modes;
        }
    }
    public static final class AspectAmmunition implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Ammunition; }
        public AspectAmmunition() { }
    }
    public static final class AspectSuperWeaponShadow implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.SuperWeaponShadow; }
        public AspectSuperWeaponShadow() { }
    }
    public static final class AspectWormMovement implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.WormMovement; }
        public AspectWormMovement() { }
    }
    public static final class AspectNPCTag implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.NPCTag; }
        public AspectNPCTag() { }
    }
    public static final class AspectPlayerKit implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.PlayerKit; }
        public AspectPlayerKit() { }
    }
    public static final class AspectLoot implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Loot; }
        public AspectLoot() { }
    }
    public static final class AspectImmunity implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Immunity; }
        public AspectImmunity() { }
    }
    public static final class AspectTurret implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Turret; }
        public AspectTurret() { }
    }
    public static final class AspectTunnel implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Tunnel; }
        public AspectTunnel() { }
    }
    public static final class AspectMountBarrier implements  Aspect {
        @JsonProperty(required = true)
        private MountStateHolder state;
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.MountBarrier; }
        public MountStateHolder getState() { return state; }
        public void setState(MountStateHolder v) { this.state = v; }
        public AspectMountBarrier() { }
        public AspectMountBarrier(MountStateHolder state) {
            this.state = state;
        }
    }
    public static final class AspectSpellMemory implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.SpellMemory; }
        public AspectSpellMemory() { }
    }
    public static final class AspectPortal implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Portal; }
        public AspectPortal() { }
    }
    public static final class AspectHate implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Hate; }
        public AspectHate() { }
    }
    public static final class AspectBarrierGate implements  Aspect {
        @JsonProperty(required = true)
        private boolean open;
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.BarrierGate; }
        public boolean getOpen() { return open; }
        public void setOpen(boolean v) { this.open = v; }
        public AspectBarrierGate() { }
        public AspectBarrierGate(boolean open) {
            this.open = open;
        }
    }
    public static final class AspectAttackable implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Attackable; }
        public AspectAttackable() { }
    }
    public static final class AspectSquadRefill implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.SquadRefill; }
        public AspectSquadRefill() { }
    }
    public static final class AspectPortalExit implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.PortalExit; }
        public AspectPortalExit() { }
    }
    /**  When building / barrier is under construction it has this aspect. */
    public static final class AspectConstructionData implements  Aspect {
        @JsonProperty(required = true)
        private TickCount refresh_count_remaining;
        @JsonProperty(required = true)
        private TickCount refresh_count_total;
        @JsonProperty(required = true)
        private float health_per_build_update_trigger;
        @JsonProperty(required = true)
        private float remaining_health_to_add;
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.ConstructionData; }
        public TickCount getRefreshCountRemaining() { return refresh_count_remaining; }
        public void setRefreshCountRemaining(TickCount v) { this.refresh_count_remaining = v; }
        public TickCount getRefreshCountTotal() { return refresh_count_total; }
        public void setRefreshCountTotal(TickCount v) { this.refresh_count_total = v; }
        public float getHealthPerBuildUpdateTrigger() { return health_per_build_update_trigger; }
        public void setHealthPerBuildUpdateTrigger(float v) { this.health_per_build_update_trigger = v; }
        public float getRemainingHealthToAdd() { return remaining_health_to_add; }
        public void setRemainingHealthToAdd(float v) { this.remaining_health_to_add = v; }
        /**  When building / barrier is under construction it has this aspect. */
        public AspectConstructionData() { }
        /**  When building / barrier is under construction it has this aspect. */
        public AspectConstructionData(TickCount refresh_count_remaining, TickCount refresh_count_total, float health_per_build_update_trigger, float remaining_health_to_add) {
            this.refresh_count_remaining = refresh_count_remaining;
            this.refresh_count_total = refresh_count_total;
            this.health_per_build_update_trigger = health_per_build_update_trigger;
            this.remaining_health_to_add = remaining_health_to_add;
        }
    }
    public static final class AspectSuperWeaponShadowBomb implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.SuperWeaponShadowBomb; }
        public AspectSuperWeaponShadowBomb() { }
    }
    public static final class AspectRepairBarrierSet implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.RepairBarrierSet; }
        public AspectRepairBarrierSet() { }
    }
    public static final class AspectConstructionRepair implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.ConstructionRepair; }
        public AspectConstructionRepair() { }
    }
    public static final class AspectFollower implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Follower; }
        public AspectFollower() { }
    }
    public static final class AspectCollisionBase implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.CollisionBase; }
        public AspectCollisionBase() { }
    }
    public static final class AspectEditorUniqueID implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.EditorUniqueID; }
        public AspectEditorUniqueID() { }
    }
    public static final class AspectRoam implements  Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() { return AspectType.Roam; }
        public AspectRoam() { }
    }
    /**  Most of the aspects do not contain data, if you think any of them would contain something,
     *  and you would want to use it, let me know, and I will add it
     */
    public static class AspectHolder {
        /**  Used by *mostly* power wells */
        @JsonProperty(value = "PowerProduction")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectPowerProduction powerProduction;
        /**  Health of an entity. */
        @JsonProperty(value = "Health")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectHealth health;
        @JsonProperty(value = "Combat")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectCombat combat;
        @JsonProperty(value = "ModeChange")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectModeChange modeChange;
        @JsonProperty(value = "Ammunition")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectAmmunition ammunition;
        @JsonProperty(value = "SuperWeaponShadow")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectSuperWeaponShadow superWeaponShadow;
        @JsonProperty(value = "WormMovement")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectWormMovement wormMovement;
        @JsonProperty(value = "NPCTag")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectNPCTag npcTag;
        @JsonProperty(value = "PlayerKit")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectPlayerKit playerKit;
        @JsonProperty(value = "Loot")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectLoot loot;
        @JsonProperty(value = "Immunity")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectImmunity immunity;
        @JsonProperty(value = "Turret")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectTurret turret;
        @JsonProperty(value = "Tunnel")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectTunnel tunnel;
        @JsonProperty(value = "MountBarrier")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectMountBarrier mountBarrier;
        @JsonProperty(value = "SpellMemory")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectSpellMemory spellMemory;
        @JsonProperty(value = "Portal")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectPortal portal;
        @JsonProperty(value = "Hate")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectHate hate;
        @JsonProperty(value = "BarrierGate")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectBarrierGate barrierGate;
        @JsonProperty(value = "Attackable")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectAttackable attackable;
        @JsonProperty(value = "SquadRefill")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectSquadRefill squadRefill;
        @JsonProperty(value = "PortalExit")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectPortalExit portalExit;
        /**  When building / barrier is under construction it has this aspect. */
        @JsonProperty(value = "ConstructionData")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectConstructionData constructionData;
        @JsonProperty(value = "SuperWeaponShadowBomb")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectSuperWeaponShadowBomb superWeaponShadowBomb;
        @JsonProperty(value = "RepairBarrierSet")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectRepairBarrierSet repairBarrierSet;
        @JsonProperty(value = "ConstructionRepair")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectConstructionRepair constructionRepair;
        @JsonProperty(value = "Follower")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectFollower follower;
        @JsonProperty(value = "CollisionBase")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectCollisionBase collisionBase;
        @JsonProperty(value = "EditorUniqueID")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectEditorUniqueID editorUniqueId;
        @JsonProperty(value = "Roam")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectRoam roam;
        @JsonIgnore
        public Aspect get() {
            if (powerProduction != null) {
                return powerProduction;
            }
            else if (health != null) {
                return health;
            }
            else if (combat != null) {
                return combat;
            }
            else if (modeChange != null) {
                return modeChange;
            }
            else if (ammunition != null) {
                return ammunition;
            }
            else if (superWeaponShadow != null) {
                return superWeaponShadow;
            }
            else if (wormMovement != null) {
                return wormMovement;
            }
            else if (npcTag != null) {
                return npcTag;
            }
            else if (playerKit != null) {
                return playerKit;
            }
            else if (loot != null) {
                return loot;
            }
            else if (immunity != null) {
                return immunity;
            }
            else if (turret != null) {
                return turret;
            }
            else if (tunnel != null) {
                return tunnel;
            }
            else if (mountBarrier != null) {
                return mountBarrier;
            }
            else if (spellMemory != null) {
                return spellMemory;
            }
            else if (portal != null) {
                return portal;
            }
            else if (hate != null) {
                return hate;
            }
            else if (barrierGate != null) {
                return barrierGate;
            }
            else if (attackable != null) {
                return attackable;
            }
            else if (squadRefill != null) {
                return squadRefill;
            }
            else if (portalExit != null) {
                return portalExit;
            }
            else if (constructionData != null) {
                return constructionData;
            }
            else if (superWeaponShadowBomb != null) {
                return superWeaponShadowBomb;
            }
            else if (repairBarrierSet != null) {
                return repairBarrierSet;
            }
            else if (constructionRepair != null) {
                return constructionRepair;
            }
            else if (follower != null) {
                return follower;
            }
            else if (collisionBase != null) {
                return collisionBase;
            }
            else if (editorUniqueId != null) {
                return editorUniqueId;
            }
            else if (roam != null) {
                return roam;
            }
            else {
                throw new IllegalStateException("AspectHolder doesn't contain any Aspect. Check implementation and API!");
            }
        }
        public AspectHolder() { }
        public AspectHolder(Aspect v) {
            Objects.requireNonNull(v, "Aspect must not be null");
            switch (v.getType()) {
                case AspectType.PowerProduction:
                    this.powerProduction = (AspectPowerProduction) v;
                    break;
                case AspectType.Health:
                    this.health = (AspectHealth) v;
                    break;
                case AspectType.Combat:
                    this.combat = (AspectCombat) v;
                    break;
                case AspectType.ModeChange:
                    this.modeChange = (AspectModeChange) v;
                    break;
                case AspectType.Ammunition:
                    this.ammunition = (AspectAmmunition) v;
                    break;
                case AspectType.SuperWeaponShadow:
                    this.superWeaponShadow = (AspectSuperWeaponShadow) v;
                    break;
                case AspectType.WormMovement:
                    this.wormMovement = (AspectWormMovement) v;
                    break;
                case AspectType.NPCTag:
                    this.npcTag = (AspectNPCTag) v;
                    break;
                case AspectType.PlayerKit:
                    this.playerKit = (AspectPlayerKit) v;
                    break;
                case AspectType.Loot:
                    this.loot = (AspectLoot) v;
                    break;
                case AspectType.Immunity:
                    this.immunity = (AspectImmunity) v;
                    break;
                case AspectType.Turret:
                    this.turret = (AspectTurret) v;
                    break;
                case AspectType.Tunnel:
                    this.tunnel = (AspectTunnel) v;
                    break;
                case AspectType.MountBarrier:
                    this.mountBarrier = (AspectMountBarrier) v;
                    break;
                case AspectType.SpellMemory:
                    this.spellMemory = (AspectSpellMemory) v;
                    break;
                case AspectType.Portal:
                    this.portal = (AspectPortal) v;
                    break;
                case AspectType.Hate:
                    this.hate = (AspectHate) v;
                    break;
                case AspectType.BarrierGate:
                    this.barrierGate = (AspectBarrierGate) v;
                    break;
                case AspectType.Attackable:
                    this.attackable = (AspectAttackable) v;
                    break;
                case AspectType.SquadRefill:
                    this.squadRefill = (AspectSquadRefill) v;
                    break;
                case AspectType.PortalExit:
                    this.portalExit = (AspectPortalExit) v;
                    break;
                case AspectType.ConstructionData:
                    this.constructionData = (AspectConstructionData) v;
                    break;
                case AspectType.SuperWeaponShadowBomb:
                    this.superWeaponShadowBomb = (AspectSuperWeaponShadowBomb) v;
                    break;
                case AspectType.RepairBarrierSet:
                    this.repairBarrierSet = (AspectRepairBarrierSet) v;
                    break;
                case AspectType.ConstructionRepair:
                    this.constructionRepair = (AspectConstructionRepair) v;
                    break;
                case AspectType.Follower:
                    this.follower = (AspectFollower) v;
                    break;
                case AspectType.CollisionBase:
                    this.collisionBase = (AspectCollisionBase) v;
                    break;
                case AspectType.EditorUniqueID:
                    this.editorUniqueId = (AspectEditorUniqueID) v;
                    break;
                case AspectType.Roam:
                    this.roam = (AspectRoam) v;
                    break;
                default: throw new IllegalArgumentException("Unknown Aspect " + v.getType());
            }
        }
        public AspectPowerProduction getPowerProduction() { return powerProduction; }
        public AspectHealth getHealth() { return health; }
        public AspectCombat getCombat() { return combat; }
        public AspectModeChange getModeChange() { return modeChange; }
        public AspectAmmunition getAmmunition() { return ammunition; }
        public AspectSuperWeaponShadow getSuperWeaponShadow() { return superWeaponShadow; }
        public AspectWormMovement getWormMovement() { return wormMovement; }
        public AspectNPCTag getNpcTag() { return npcTag; }
        public AspectPlayerKit getPlayerKit() { return playerKit; }
        public AspectLoot getLoot() { return loot; }
        public AspectImmunity getImmunity() { return immunity; }
        public AspectTurret getTurret() { return turret; }
        public AspectTunnel getTunnel() { return tunnel; }
        public AspectMountBarrier getMountBarrier() { return mountBarrier; }
        public AspectSpellMemory getSpellMemory() { return spellMemory; }
        public AspectPortal getPortal() { return portal; }
        public AspectHate getHate() { return hate; }
        public AspectBarrierGate getBarrierGate() { return barrierGate; }
        public AspectAttackable getAttackable() { return attackable; }
        public AspectSquadRefill getSquadRefill() { return squadRefill; }
        public AspectPortalExit getPortalExit() { return portalExit; }
        public AspectConstructionData getConstructionData() { return constructionData; }
        public AspectSuperWeaponShadowBomb getSuperWeaponShadowBomb() { return superWeaponShadowBomb; }
        public AspectRepairBarrierSet getRepairBarrierSet() { return repairBarrierSet; }
        public AspectConstructionRepair getConstructionRepair() { return constructionRepair; }
        public AspectFollower getFollower() { return follower; }
        public AspectCollisionBase getCollisionBase() { return collisionBase; }
        public AspectEditorUniqueID getEditorUniqueId() { return editorUniqueId; }
        public AspectRoam getRoam() { return roam; }
    }

    /**  Simplified version of how many monuments of each color player have */
    public static class Orbs {
        @JsonProperty(required = true)
        private byte shadow;
        @JsonProperty(required = true)
        private byte nature;
        @JsonProperty(required = true)
        private byte frost;
        @JsonProperty(required = true)
        private byte fire;
        /**  Can be used instead of any color, and then changes to color of first token on the used card. */
        @JsonProperty(required = true)
        private byte starting;
        /**  Can be used only for colorless tokens on the card. (Curse Orb changes colored orb to white one) */
        @JsonProperty(required = true)
        private byte white;
        /**  Can be used as any color. Only provided by map scripts. */
        @JsonProperty(required = true)
        private byte all;
        public byte getShadow() { return shadow; }
        public void setShadow(byte v) { this.shadow = v; }
        public byte getNature() { return nature; }
        public void setNature(byte v) { this.nature = v; }
        public byte getFrost() { return frost; }
        public void setFrost(byte v) { this.frost = v; }
        public byte getFire() { return fire; }
        public void setFire(byte v) { this.fire = v; }
        public byte getStarting() { return starting; }
        public void setStarting(byte v) { this.starting = v; }
        public byte getWhite() { return white; }
        public void setWhite(byte v) { this.white = v; }
        public byte getAll() { return all; }
        public void setAll(byte v) { this.all = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Orbs that = (Orbs) o;
            return getShadow() == that.getShadow() && getNature() == that.getNature() && getFrost() == that.getFrost() && getFire() == that.getFire() && getStarting() == that.getStarting() && getWhite() == that.getWhite() && getAll() == that.getAll();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getShadow(), getNature(), getFrost(), getFire(), getStarting(), getWhite(), getAll());
        }
        @Override
        public String toString() {
            return "{" + "shadow: " + shadow + ", nature: " + nature + ", frost: " + frost + ", fire: " + fire + ", starting: " + starting + ", white: " + white + ", all: " + all + "}";
        }
        /**  Simplified version of how many monuments of each color player have */
        public Orbs() { }
        /**  Simplified version of how many monuments of each color player have */
        public Orbs(byte shadow, byte nature, byte frost, byte fire, byte starting, byte white, byte all) {
            this.shadow = shadow;
            this.nature = nature;
            this.frost = frost;
            this.fire = fire;
            this.starting = starting;
            this.white = white;
            this.all = all;
        }
    }

    /**  Technically it is specific case of `Entity`, but we decided to move players out,
     *  and move few fields up like position and owning player id
     */
    public static class PlayerEntity {
        /**  Unique id of the entity */
        @JsonProperty(required = true)
        private EntityId id;
        /**  List of effects the entity have. */
        @JsonProperty(required = true)
        private AbilityEffect[] effects;
        /**  List of aspects entity have. */
        @JsonProperty(required = true)
        private AspectHolder[] aspects;
        @JsonProperty(required = true)
        private byte team;
        @JsonProperty(required = true)
        private float power;
        @JsonProperty(required = true)
        private float void_power;
        @JsonProperty(required = true)
        private short population_count;
        @JsonProperty(required = true)
        private String name;
        @JsonProperty(required = true)
        private Orbs orbs;
        public EntityId getId() { return id; }
        public void setId(EntityId v) { this.id = v; }
        public AbilityEffect[] getEffects() { return effects; }
        public void setEffects(AbilityEffect[] v) { this.effects = v; }
        public AspectHolder[] getAspects() { return aspects; }
        public void setAspects(AspectHolder[] v) { this.aspects = v; }
        public byte getTeam() { return team; }
        public void setTeam(byte v) { this.team = v; }
        public float getPower() { return power; }
        public void setPower(float v) { this.power = v; }
        public float getVoidPower() { return void_power; }
        public void setVoidPower(float v) { this.void_power = v; }
        public short getPopulationCount() { return population_count; }
        public void setPopulationCount(short v) { this.population_count = v; }
        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public Orbs getOrbs() { return orbs; }
        public void setOrbs(Orbs v) { this.orbs = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlayerEntity that = (PlayerEntity) o;
            return getId() == that.getId() && getEffects() == that.getEffects() && getAspects() == that.getAspects() && getTeam() == that.getTeam() && getPower() == that.getPower() && getVoidPower() == that.getVoidPower() && getPopulationCount() == that.getPopulationCount() && getName() == that.getName() && getOrbs() == that.getOrbs();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getId(), getEffects(), getAspects(), getTeam(), getPower(), getVoidPower(), getPopulationCount(), getName(), getOrbs());
        }
        @Override
        public String toString() {
            return "{" + "id: " + id + ", effects: " + effects + ", aspects: " + aspects + ", team: " + team + ", power: " + power + ", void_power: " + void_power + ", population_count: " + population_count + ", name: " + name + ", orbs: " + orbs + "}";
        }
        /**  Technically it is specific case of `Entity`, but we decided to move players out,
         *  and move few fields up like position and owning player id
         */
        public PlayerEntity() { }
        /**  Technically it is specific case of `Entity`, but we decided to move players out,
         *  and move few fields up like position and owning player id
         */
        public PlayerEntity(EntityId id, AbilityEffect[] effects, AspectHolder[] aspects, byte team, float power, float void_power, short population_count, String name, Orbs orbs) {
            this.id = id;
            this.effects = effects;
            this.aspects = aspects;
            this.team = team;
            this.power = power;
            this.void_power = void_power;
            this.population_count = population_count;
            this.name = name;
            this.orbs = orbs;
        }
    }

    public static class MatchPlayer {
        /**  Name of player. */
        @JsonProperty(required = true)
        private String name;
        /**  Deck used by that player.
         *  TODO Due to technical difficulties might be empty.
         */
        @JsonProperty(required = true)
        private Deck deck;
        /**  entity controlled by this player */
        @JsonProperty(required = true)
        private PlayerEntity entity;
        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public Deck getDeck() { return deck; }
        public void setDeck(Deck v) { this.deck = v; }
        public PlayerEntity getEntity() { return entity; }
        public void setEntity(PlayerEntity v) { this.entity = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MatchPlayer that = (MatchPlayer) o;
            return getName() == that.getName() && getDeck() == that.getDeck() && getEntity() == that.getEntity();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getName(), getDeck(), getEntity());
        }
        @Override
        public String toString() {
            return "{" + "name: " + name + ", deck: " + deck + ", entity: " + entity + "}";
        }
        public MatchPlayer() { }
        public MatchPlayer(String name, Deck deck, PlayerEntity entity) {
            this.name = name;
            this.deck = deck;
            this.entity = entity;
        }
    }

    public enum JobType {
        NoJob,
        Idle,
        Goto,
        AttackMelee,
        CastSpell,
        Die,
        Talk,
        ScriptTalk,
        Freeze,
        Spawn,
        Cheer,
        AttackSquad,
        CastSpellSquad,
        PushBack,
        Stampede,
        BarrierCrush,
        BarrierGateToggle,
        FlameThrower,
        Construct,
        Crush,
        MountBarrierSquad,
        MountBarrier,
        ModeChangeSquad,
        ModeChange,
        SacrificeSquad,
        UsePortalSquad,
        Channel,
        SpawnSquad,
        LootTargetSquad,
        Morph,
        Unknown;
    }
    /** Marker fo all Job implementations */
    public interface Job extends MultiType<JobType> {}
    public static final class JobNoJob implements  Job {
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.NoJob; }
        public JobNoJob() { }
    }
    public static final class JobIdle implements  Job {
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Idle; }
        public JobIdle() { }
    }
    public static final class JobGoto implements  Job {
        @JsonProperty(required = true)
        private Position2DWithOrientation[] waypoints;
        @JsonProperty(required = true)
        private EntityId target_entity_id;
        @JsonProperty(required = true)
        private WalkMode walk_mode;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Goto; }
        public Position2DWithOrientation[] getWaypoints() { return waypoints; }
        public void setWaypoints(Position2DWithOrientation[] v) { this.waypoints = v; }
        public EntityId getTargetEntityId() { return target_entity_id; }
        public void setTargetEntityId(EntityId v) { this.target_entity_id = v; }
        public WalkMode getWalkMode() { return walk_mode; }
        public void setWalkMode(WalkMode v) { this.walk_mode = v; }
        public JobGoto() { }
        public JobGoto(Position2DWithOrientation[] waypoints, EntityId target_entity_id, WalkMode walk_mode) {
            this.waypoints = waypoints;
            this.target_entity_id = target_entity_id;
            this.walk_mode = walk_mode;
        }
    }
    public static final class JobAttackMelee implements  Job {
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(required = true)
        private boolean use_force_goto;
        @JsonProperty(required = true)
        private boolean no_move;
        @JsonProperty(required = true)
        private float too_close_range;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.AttackMelee; }
        public TargetHolder getTarget() { return target; }
        public void setTarget(TargetHolder v) { this.target = v; }
        public boolean getUseForceGoto() { return use_force_goto; }
        public void setUseForceGoto(boolean v) { this.use_force_goto = v; }
        public boolean getNoMove() { return no_move; }
        public void setNoMove(boolean v) { this.no_move = v; }
        public float getTooCloseRange() { return too_close_range; }
        public void setTooCloseRange(float v) { this.too_close_range = v; }
        public JobAttackMelee() { }
        public JobAttackMelee(TargetHolder target, boolean use_force_goto, boolean no_move, float too_close_range) {
            this.target = target;
            this.use_force_goto = use_force_goto;
            this.no_move = no_move;
            this.too_close_range = too_close_range;
        }
    }
    public static final class JobCastSpell implements  Job {
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(required = true)
        private SpellId spell_id;
        @JsonProperty(required = true)
        private boolean use_force_goto;
        @JsonProperty(required = true)
        private boolean no_move;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.CastSpell; }
        public TargetHolder getTarget() { return target; }
        public void setTarget(TargetHolder v) { this.target = v; }
        public SpellId getSpellId() { return spell_id; }
        public void setSpellId(SpellId v) { this.spell_id = v; }
        public boolean getUseForceGoto() { return use_force_goto; }
        public void setUseForceGoto(boolean v) { this.use_force_goto = v; }
        public boolean getNoMove() { return no_move; }
        public void setNoMove(boolean v) { this.no_move = v; }
        public JobCastSpell() { }
        public JobCastSpell(TargetHolder target, SpellId spell_id, boolean use_force_goto, boolean no_move) {
            this.target = target;
            this.spell_id = spell_id;
            this.use_force_goto = use_force_goto;
            this.no_move = no_move;
        }
    }
    public static final class JobDie implements  Job {
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Die; }
        public JobDie() { }
    }
    public static final class JobTalk implements  Job {
        @JsonProperty(required = true)
        private EntityId target;
        @JsonProperty(required = true)
        private boolean walk_to_target;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Talk; }
        public EntityId getTarget() { return target; }
        public void setTarget(EntityId v) { this.target = v; }
        public boolean getWalkToTarget() { return walk_to_target; }
        public void setWalkToTarget(boolean v) { this.walk_to_target = v; }
        public JobTalk() { }
        public JobTalk(EntityId target, boolean walk_to_target) {
            this.target = target;
            this.walk_to_target = walk_to_target;
        }
    }
    public static final class JobScriptTalk implements  Job {
        @JsonProperty(required = true)
        private boolean hide_weapon;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.ScriptTalk; }
        public boolean getHideWeapon() { return hide_weapon; }
        public void setHideWeapon(boolean v) { this.hide_weapon = v; }
        public JobScriptTalk() { }
        public JobScriptTalk(boolean hide_weapon) {
            this.hide_weapon = hide_weapon;
        }
    }
    public static final class JobFreeze implements  Job {
        @JsonProperty(required = true)
        private Tick end_step;
        @JsonProperty(required = true)
        private EntityId source;
        @JsonProperty(required = true)
        private SpellId spell_id;
        @JsonProperty(required = true)
        private TickCount duration;
        @JsonProperty(required = true)
        private TickCount delay_ability;
        @JsonProperty(required = true)
        private AbilityId[] ability_id_while_frozen;
        @JsonProperty(required = true)
        private AbilityId[] ability_id_delayed;
        @JsonProperty(required = true)
        private AbilityLine ability_line_id_cancel_on_start;
        @JsonProperty(required = true)
        private boolean pushback_immunity;
        @JsonProperty(required = true)
        private int mode;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Freeze; }
        public Tick getEndStep() { return end_step; }
        public void setEndStep(Tick v) { this.end_step = v; }
        public EntityId getSource() { return source; }
        public void setSource(EntityId v) { this.source = v; }
        public SpellId getSpellId() { return spell_id; }
        public void setSpellId(SpellId v) { this.spell_id = v; }
        public TickCount getDuration() { return duration; }
        public void setDuration(TickCount v) { this.duration = v; }
        public TickCount getDelayAbility() { return delay_ability; }
        public void setDelayAbility(TickCount v) { this.delay_ability = v; }
        public AbilityId[] getAbilityIdWhileFrozen() { return ability_id_while_frozen; }
        public void setAbilityIdWhileFrozen(AbilityId[] v) { this.ability_id_while_frozen = v; }
        public AbilityId[] getAbilityIdDelayed() { return ability_id_delayed; }
        public void setAbilityIdDelayed(AbilityId[] v) { this.ability_id_delayed = v; }
        public AbilityLine getAbilityLineIdCancelOnStart() { return ability_line_id_cancel_on_start; }
        public void setAbilityLineIdCancelOnStart(AbilityLine v) { this.ability_line_id_cancel_on_start = v; }
        public boolean getPushbackImmunity() { return pushback_immunity; }
        public void setPushbackImmunity(boolean v) { this.pushback_immunity = v; }
        public int getMode() { return mode; }
        public void setMode(int v) { this.mode = v; }
        public JobFreeze() { }
        public JobFreeze(Tick end_step, EntityId source, SpellId spell_id, TickCount duration, TickCount delay_ability, AbilityId[] ability_id_while_frozen, AbilityId[] ability_id_delayed, AbilityLine ability_line_id_cancel_on_start, boolean pushback_immunity, int mode) {
            this.end_step = end_step;
            this.source = source;
            this.spell_id = spell_id;
            this.duration = duration;
            this.delay_ability = delay_ability;
            this.ability_id_while_frozen = ability_id_while_frozen;
            this.ability_id_delayed = ability_id_delayed;
            this.ability_line_id_cancel_on_start = ability_line_id_cancel_on_start;
            this.pushback_immunity = pushback_immunity;
            this.mode = mode;
        }
    }
    public static final class JobSpawn implements  Job {
        @JsonProperty(required = true)
        private TickCount duration;
        @JsonProperty(required = true)
        private Tick end_step;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Spawn; }
        public TickCount getDuration() { return duration; }
        public void setDuration(TickCount v) { this.duration = v; }
        public Tick getEndStep() { return end_step; }
        public void setEndStep(Tick v) { this.end_step = v; }
        public JobSpawn() { }
        public JobSpawn(TickCount duration, Tick end_step) {
            this.duration = duration;
            this.end_step = end_step;
        }
    }
    public static final class JobCheer implements  Job {
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Cheer; }
        public JobCheer() { }
    }
    public static final class JobAttackSquad implements  Job {
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(required = true)
        private byte weapon_type;
        @JsonProperty(required = true)
        private float damage;
        @JsonProperty(required = true)
        private float range_min;
        @JsonProperty(required = true)
        private float range_max;
        @JsonProperty(required = true)
        private SpellId attack_spell;
        @JsonProperty(required = true)
        private boolean use_force_goto;
        @JsonProperty(required = true)
        private float operation_range;
        @JsonProperty(required = true)
        private boolean no_move;
        @JsonProperty(required = true)
        private boolean was_in_attack;
        @JsonProperty(required = true)
        private boolean melee_attack;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.AttackSquad; }
        public TargetHolder getTarget() { return target; }
        public void setTarget(TargetHolder v) { this.target = v; }
        public byte getWeaponType() { return weapon_type; }
        public void setWeaponType(byte v) { this.weapon_type = v; }
        public float getDamage() { return damage; }
        public void setDamage(float v) { this.damage = v; }
        public float getRangeMin() { return range_min; }
        public void setRangeMin(float v) { this.range_min = v; }
        public float getRangeMax() { return range_max; }
        public void setRangeMax(float v) { this.range_max = v; }
        public SpellId getAttackSpell() { return attack_spell; }
        public void setAttackSpell(SpellId v) { this.attack_spell = v; }
        public boolean getUseForceGoto() { return use_force_goto; }
        public void setUseForceGoto(boolean v) { this.use_force_goto = v; }
        public float getOperationRange() { return operation_range; }
        public void setOperationRange(float v) { this.operation_range = v; }
        public boolean getNoMove() { return no_move; }
        public void setNoMove(boolean v) { this.no_move = v; }
        public boolean getWasInAttack() { return was_in_attack; }
        public void setWasInAttack(boolean v) { this.was_in_attack = v; }
        public boolean getMeleeAttack() { return melee_attack; }
        public void setMeleeAttack(boolean v) { this.melee_attack = v; }
        public JobAttackSquad() { }
        public JobAttackSquad(TargetHolder target, byte weapon_type, float damage, float range_min, float range_max, SpellId attack_spell, boolean use_force_goto, float operation_range, boolean no_move, boolean was_in_attack, boolean melee_attack) {
            this.target = target;
            this.weapon_type = weapon_type;
            this.damage = damage;
            this.range_min = range_min;
            this.range_max = range_max;
            this.attack_spell = attack_spell;
            this.use_force_goto = use_force_goto;
            this.operation_range = operation_range;
            this.no_move = no_move;
            this.was_in_attack = was_in_attack;
            this.melee_attack = melee_attack;
        }
    }
    public static final class JobCastSpellSquad implements  Job {
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(required = true)
        private SpellId spell_id;
        @JsonProperty(required = true)
        private boolean use_force_goto;
        @JsonProperty(required = true)
        private boolean spell_fired;
        @JsonProperty(required = true)
        private boolean spell_per_source_entity;
        @JsonProperty(required = true)
        private boolean was_in_attack;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.CastSpellSquad; }
        public TargetHolder getTarget() { return target; }
        public void setTarget(TargetHolder v) { this.target = v; }
        public SpellId getSpellId() { return spell_id; }
        public void setSpellId(SpellId v) { this.spell_id = v; }
        public boolean getUseForceGoto() { return use_force_goto; }
        public void setUseForceGoto(boolean v) { this.use_force_goto = v; }
        public boolean getSpellFired() { return spell_fired; }
        public void setSpellFired(boolean v) { this.spell_fired = v; }
        public boolean getSpellPerSourceEntity() { return spell_per_source_entity; }
        public void setSpellPerSourceEntity(boolean v) { this.spell_per_source_entity = v; }
        public boolean getWasInAttack() { return was_in_attack; }
        public void setWasInAttack(boolean v) { this.was_in_attack = v; }
        public JobCastSpellSquad() { }
        public JobCastSpellSquad(TargetHolder target, SpellId spell_id, boolean use_force_goto, boolean spell_fired, boolean spell_per_source_entity, boolean was_in_attack) {
            this.target = target;
            this.spell_id = spell_id;
            this.use_force_goto = use_force_goto;
            this.spell_fired = spell_fired;
            this.spell_per_source_entity = spell_per_source_entity;
            this.was_in_attack = was_in_attack;
        }
    }
    public static final class JobPushBack implements  Job {
        @JsonProperty(required = true)
        private Position2D start_coord;
        @JsonProperty(required = true)
        private Position2D target_coord;
        @JsonProperty(required = true)
        private float speed;
        @JsonProperty(required = true)
        private float rotation_speed;
        @JsonProperty(required = true)
        private float damage;
        @JsonProperty(required = true)
        private EntityId source;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.PushBack; }
        public Position2D getStartCoord() { return start_coord; }
        public void setStartCoord(Position2D v) { this.start_coord = v; }
        public Position2D getTargetCoord() { return target_coord; }
        public void setTargetCoord(Position2D v) { this.target_coord = v; }
        public float getSpeed() { return speed; }
        public void setSpeed(float v) { this.speed = v; }
        public float getRotationSpeed() { return rotation_speed; }
        public void setRotationSpeed(float v) { this.rotation_speed = v; }
        public float getDamage() { return damage; }
        public void setDamage(float v) { this.damage = v; }
        public EntityId getSource() { return source; }
        public void setSource(EntityId v) { this.source = v; }
        public JobPushBack() { }
        public JobPushBack(Position2D start_coord, Position2D target_coord, float speed, float rotation_speed, float damage, EntityId source) {
            this.start_coord = start_coord;
            this.target_coord = target_coord;
            this.speed = speed;
            this.rotation_speed = rotation_speed;
            this.damage = damage;
            this.source = source;
        }
    }
    public static final class JobStampede implements  Job {
        @JsonProperty(required = true)
        private SpellId spell;
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(required = true)
        private Position2D start_coord;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Stampede; }
        public SpellId getSpell() { return spell; }
        public void setSpell(SpellId v) { this.spell = v; }
        public TargetHolder getTarget() { return target; }
        public void setTarget(TargetHolder v) { this.target = v; }
        public Position2D getStartCoord() { return start_coord; }
        public void setStartCoord(Position2D v) { this.start_coord = v; }
        public JobStampede() { }
        public JobStampede(SpellId spell, TargetHolder target, Position2D start_coord) {
            this.spell = spell;
            this.target = target;
            this.start_coord = start_coord;
        }
    }
    public static final class JobBarrierCrush implements  Job {
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.BarrierCrush; }
        public JobBarrierCrush() { }
    }
    public static final class JobBarrierGateToggle implements  Job {
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.BarrierGateToggle; }
        public JobBarrierGateToggle() { }
    }
    public static final class JobFlameThrower implements  Job {
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(required = true)
        private SpellId spell_id;
        @JsonProperty(required = true)
        private TickCount duration_step_init;
        @JsonProperty(required = true)
        private TickCount duration_step_shut_down;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.FlameThrower; }
        public TargetHolder getTarget() { return target; }
        public void setTarget(TargetHolder v) { this.target = v; }
        public SpellId getSpellId() { return spell_id; }
        public void setSpellId(SpellId v) { this.spell_id = v; }
        public TickCount getDurationStepInit() { return duration_step_init; }
        public void setDurationStepInit(TickCount v) { this.duration_step_init = v; }
        public TickCount getDurationStepShutDown() { return duration_step_shut_down; }
        public void setDurationStepShutDown(TickCount v) { this.duration_step_shut_down = v; }
        public JobFlameThrower() { }
        public JobFlameThrower(TargetHolder target, SpellId spell_id, TickCount duration_step_init, TickCount duration_step_shut_down) {
            this.target = target;
            this.spell_id = spell_id;
            this.duration_step_init = duration_step_init;
            this.duration_step_shut_down = duration_step_shut_down;
        }
    }
    public static final class JobConstruct implements  Job {
        @JsonProperty(required = true)
        private TickCount construction_update_steps;
        @JsonProperty(required = true)
        private TickCount construction_update_count_remaining;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Construct; }
        public TickCount getConstructionUpdateSteps() { return construction_update_steps; }
        public void setConstructionUpdateSteps(TickCount v) { this.construction_update_steps = v; }
        public TickCount getConstructionUpdateCountRemaining() { return construction_update_count_remaining; }
        public void setConstructionUpdateCountRemaining(TickCount v) { this.construction_update_count_remaining = v; }
        public JobConstruct() { }
        public JobConstruct(TickCount construction_update_steps, TickCount construction_update_count_remaining) {
            this.construction_update_steps = construction_update_steps;
            this.construction_update_count_remaining = construction_update_count_remaining;
        }
    }
    public static final class JobCrush implements  Job {
        @JsonProperty(required = true)
        private TickCount crush_steps;
        @JsonProperty(required = true)
        private TickCount entity_update_steps;
        @JsonProperty(required = true)
        private TickCount remaining_crush_steps;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Crush; }
        public TickCount getCrushSteps() { return crush_steps; }
        public void setCrushSteps(TickCount v) { this.crush_steps = v; }
        public TickCount getEntityUpdateSteps() { return entity_update_steps; }
        public void setEntityUpdateSteps(TickCount v) { this.entity_update_steps = v; }
        public TickCount getRemainingCrushSteps() { return remaining_crush_steps; }
        public void setRemainingCrushSteps(TickCount v) { this.remaining_crush_steps = v; }
        public JobCrush() { }
        public JobCrush(TickCount crush_steps, TickCount entity_update_steps, TickCount remaining_crush_steps) {
            this.crush_steps = crush_steps;
            this.entity_update_steps = entity_update_steps;
            this.remaining_crush_steps = remaining_crush_steps;
        }
    }
    public static final class JobMountBarrierSquad implements  Job {
        @JsonProperty(required = true)
        private EntityId barrier_module;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.MountBarrierSquad; }
        public EntityId getBarrierModule() { return barrier_module; }
        public void setBarrierModule(EntityId v) { this.barrier_module = v; }
        public JobMountBarrierSquad() { }
        public JobMountBarrierSquad(EntityId barrier_module) {
            this.barrier_module = barrier_module;
        }
    }
    public static final class JobMountBarrier implements  Job {
        @JsonProperty(required = true)
        private EntityId current_barrier_module;
        @JsonProperty(required = true)
        private EntityId goal_barrier_module;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.MountBarrier; }
        public EntityId getCurrentBarrierModule() { return current_barrier_module; }
        public void setCurrentBarrierModule(EntityId v) { this.current_barrier_module = v; }
        public EntityId getGoalBarrierModule() { return goal_barrier_module; }
        public void setGoalBarrierModule(EntityId v) { this.goal_barrier_module = v; }
        public JobMountBarrier() { }
        public JobMountBarrier(EntityId current_barrier_module, EntityId goal_barrier_module) {
            this.current_barrier_module = current_barrier_module;
            this.goal_barrier_module = goal_barrier_module;
        }
    }
    public static final class JobModeChangeSquad implements  Job {
        @JsonProperty(required = true)
        private ModeId new_mode;
        @JsonProperty(required = true)
        private boolean mode_change_done;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.ModeChangeSquad; }
        public ModeId getNewMode() { return new_mode; }
        public void setNewMode(ModeId v) { this.new_mode = v; }
        public boolean getModeChangeDone() { return mode_change_done; }
        public void setModeChangeDone(boolean v) { this.mode_change_done = v; }
        public JobModeChangeSquad() { }
        public JobModeChangeSquad(ModeId new_mode, boolean mode_change_done) {
            this.new_mode = new_mode;
            this.mode_change_done = mode_change_done;
        }
    }
    public static final class JobModeChange implements  Job {
        @JsonProperty(required = true)
        private ModeId new_mode;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.ModeChange; }
        public ModeId getNewMode() { return new_mode; }
        public void setNewMode(ModeId v) { this.new_mode = v; }
        public JobModeChange() { }
        public JobModeChange(ModeId new_mode) {
            this.new_mode = new_mode;
        }
    }
    public static final class JobSacrificeSquad implements  Job {
        @JsonProperty(required = true)
        private EntityId target_entity;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.SacrificeSquad; }
        public EntityId getTargetEntity() { return target_entity; }
        public void setTargetEntity(EntityId v) { this.target_entity = v; }
        public JobSacrificeSquad() { }
        public JobSacrificeSquad(EntityId target_entity) {
            this.target_entity = target_entity;
        }
    }
    public static final class JobUsePortalSquad implements  Job {
        @JsonProperty(required = true)
        private EntityId target_entity_id;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.UsePortalSquad; }
        public EntityId getTargetEntityId() { return target_entity_id; }
        public void setTargetEntityId(EntityId v) { this.target_entity_id = v; }
        public JobUsePortalSquad() { }
        public JobUsePortalSquad(EntityId target_entity_id) {
            this.target_entity_id = target_entity_id;
        }
    }
    public static final class JobChannel implements  Job {
        @JsonProperty(required = true)
        private EntityId target_squad_id;
        @JsonProperty(required = true)
        private boolean mode_target_world;
        @JsonProperty(required = true)
        private EntityId entity_id;
        @JsonProperty(required = true)
        private SpellId spell_id;
        @JsonProperty(required = true)
        private SpellId spell_id_on_target_on_finish;
        @JsonProperty(required = true)
        private SpellId spell_id_on_target_on_start;
        @JsonProperty(required = true)
        private TickCount step_duration_until_finish;
        @JsonProperty(required = true)
        private int timing_channel_start;
        @JsonProperty(required = true)
        private int timing_channel_loop;
        @JsonProperty(required = true)
        private int timing_channel_end;
        @JsonProperty(required = true)
        private float abort_on_out_of_range_squared;
        @JsonProperty(required = true)
        private boolean abort_check_failed;
        @JsonProperty(required = true)
        private boolean orientate_to_target;
        @JsonProperty(required = true)
        private TickCount orientate_to_target_max_step;
        @JsonProperty(required = true)
        private boolean abort_on_owner_get_damaged;
        @JsonProperty(required = true)
        private boolean abort_on_mode_change;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Channel; }
        public EntityId getTargetSquadId() { return target_squad_id; }
        public void setTargetSquadId(EntityId v) { this.target_squad_id = v; }
        public boolean getModeTargetWorld() { return mode_target_world; }
        public void setModeTargetWorld(boolean v) { this.mode_target_world = v; }
        public EntityId getEntityId() { return entity_id; }
        public void setEntityId(EntityId v) { this.entity_id = v; }
        public SpellId getSpellId() { return spell_id; }
        public void setSpellId(SpellId v) { this.spell_id = v; }
        public SpellId getSpellIdOnTargetOnFinish() { return spell_id_on_target_on_finish; }
        public void setSpellIdOnTargetOnFinish(SpellId v) { this.spell_id_on_target_on_finish = v; }
        public SpellId getSpellIdOnTargetOnStart() { return spell_id_on_target_on_start; }
        public void setSpellIdOnTargetOnStart(SpellId v) { this.spell_id_on_target_on_start = v; }
        public TickCount getStepDurationUntilFinish() { return step_duration_until_finish; }
        public void setStepDurationUntilFinish(TickCount v) { this.step_duration_until_finish = v; }
        public int getTimingChannelStart() { return timing_channel_start; }
        public void setTimingChannelStart(int v) { this.timing_channel_start = v; }
        public int getTimingChannelLoop() { return timing_channel_loop; }
        public void setTimingChannelLoop(int v) { this.timing_channel_loop = v; }
        public int getTimingChannelEnd() { return timing_channel_end; }
        public void setTimingChannelEnd(int v) { this.timing_channel_end = v; }
        public float getAbortOnOutOfRangeSquared() { return abort_on_out_of_range_squared; }
        public void setAbortOnOutOfRangeSquared(float v) { this.abort_on_out_of_range_squared = v; }
        public boolean getAbortCheckFailed() { return abort_check_failed; }
        public void setAbortCheckFailed(boolean v) { this.abort_check_failed = v; }
        public boolean getOrientateToTarget() { return orientate_to_target; }
        public void setOrientateToTarget(boolean v) { this.orientate_to_target = v; }
        public TickCount getOrientateToTargetMaxStep() { return orientate_to_target_max_step; }
        public void setOrientateToTargetMaxStep(TickCount v) { this.orientate_to_target_max_step = v; }
        public boolean getAbortOnOwnerGetDamaged() { return abort_on_owner_get_damaged; }
        public void setAbortOnOwnerGetDamaged(boolean v) { this.abort_on_owner_get_damaged = v; }
        public boolean getAbortOnModeChange() { return abort_on_mode_change; }
        public void setAbortOnModeChange(boolean v) { this.abort_on_mode_change = v; }
        public JobChannel() { }
        public JobChannel(EntityId target_squad_id, boolean mode_target_world, EntityId entity_id, SpellId spell_id, SpellId spell_id_on_target_on_finish, SpellId spell_id_on_target_on_start, TickCount step_duration_until_finish, int timing_channel_start, int timing_channel_loop, int timing_channel_end, float abort_on_out_of_range_squared, boolean abort_check_failed, boolean orientate_to_target, TickCount orientate_to_target_max_step, boolean abort_on_owner_get_damaged, boolean abort_on_mode_change) {
            this.target_squad_id = target_squad_id;
            this.mode_target_world = mode_target_world;
            this.entity_id = entity_id;
            this.spell_id = spell_id;
            this.spell_id_on_target_on_finish = spell_id_on_target_on_finish;
            this.spell_id_on_target_on_start = spell_id_on_target_on_start;
            this.step_duration_until_finish = step_duration_until_finish;
            this.timing_channel_start = timing_channel_start;
            this.timing_channel_loop = timing_channel_loop;
            this.timing_channel_end = timing_channel_end;
            this.abort_on_out_of_range_squared = abort_on_out_of_range_squared;
            this.abort_check_failed = abort_check_failed;
            this.orientate_to_target = orientate_to_target;
            this.orientate_to_target_max_step = orientate_to_target_max_step;
            this.abort_on_owner_get_damaged = abort_on_owner_get_damaged;
            this.abort_on_mode_change = abort_on_mode_change;
        }
    }
    public static final class JobSpawnSquad implements  Job {
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.SpawnSquad; }
        public JobSpawnSquad() { }
    }
    public static final class JobLootTargetSquad implements  Job {
        @JsonProperty(required = true)
        private EntityId target_entity_id;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.LootTargetSquad; }
        public EntityId getTargetEntityId() { return target_entity_id; }
        public void setTargetEntityId(EntityId v) { this.target_entity_id = v; }
        public JobLootTargetSquad() { }
        public JobLootTargetSquad(EntityId target_entity_id) {
            this.target_entity_id = target_entity_id;
        }
    }
    public static final class JobMorph implements  Job {
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(required = true)
        private SpellId spell;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Morph; }
        public TargetHolder getTarget() { return target; }
        public void setTarget(TargetHolder v) { this.target = v; }
        public SpellId getSpell() { return spell; }
        public void setSpell(SpellId v) { this.spell = v; }
        public JobMorph() { }
        public JobMorph(TargetHolder target, SpellId spell) {
            this.target = target;
            this.spell = spell;
        }
    }
    /**  if you see this it means we did not account for some EA's case, so please report it */
    public static final class JobUnknown implements  Job {
        @JsonProperty(required = true)
        private int id;
        @Override
        @JsonIgnore
        public JobType getType() { return JobType.Unknown; }
        public int getId() { return id; }
        public void setId(int v) { this.id = v; }
        /**  if you see this it means we did not account for some EA's case, so please report it */
        public JobUnknown() { }
        /**  if you see this it means we did not account for some EA's case, so please report it */
        public JobUnknown(int id) {
            this.id = id;
        }
    }
    /**  With the way the game works, I would not be surprised, if this will cause more issues.
     *  If the game crashes send the log to `Kubik` it probably mean some field in
     *  one of the `Job`s needs to be `Option`.
     */
    public static class JobHolder {
        @JsonProperty(value = "NoJob")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobNoJob noJob;
        @JsonProperty(value = "Idle")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobIdle idle;
        @JsonProperty(value = "Goto")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobGoto gotoJavaField;
        @JsonProperty(value = "AttackMelee")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobAttackMelee attackMelee;
        @JsonProperty(value = "CastSpell")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobCastSpell castSpell;
        @JsonProperty(value = "Die")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobDie die;
        @JsonProperty(value = "Talk")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobTalk talk;
        @JsonProperty(value = "ScriptTalk")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobScriptTalk scriptTalk;
        @JsonProperty(value = "Freeze")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobFreeze freeze;
        @JsonProperty(value = "Spawn")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobSpawn spawn;
        @JsonProperty(value = "Cheer")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobCheer cheer;
        @JsonProperty(value = "AttackSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobAttackSquad attackSquad;
        @JsonProperty(value = "CastSpellSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobCastSpellSquad castSpellSquad;
        @JsonProperty(value = "PushBack")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobPushBack pushBack;
        @JsonProperty(value = "Stampede")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobStampede stampede;
        @JsonProperty(value = "BarrierCrush")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobBarrierCrush barrierCrush;
        @JsonProperty(value = "BarrierGateToggle")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobBarrierGateToggle barrierGateToggle;
        @JsonProperty(value = "FlameThrower")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobFlameThrower flameThrower;
        @JsonProperty(value = "Construct")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobConstruct construct;
        @JsonProperty(value = "Crush")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobCrush crush;
        @JsonProperty(value = "MountBarrierSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobMountBarrierSquad mountBarrierSquad;
        @JsonProperty(value = "MountBarrier")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobMountBarrier mountBarrier;
        @JsonProperty(value = "ModeChangeSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobModeChangeSquad modeChangeSquad;
        @JsonProperty(value = "ModeChange")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobModeChange modeChange;
        @JsonProperty(value = "SacrificeSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobSacrificeSquad sacrificeSquad;
        @JsonProperty(value = "UsePortalSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobUsePortalSquad usePortalSquad;
        @JsonProperty(value = "Channel")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobChannel channel;
        @JsonProperty(value = "SpawnSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobSpawnSquad spawnSquad;
        @JsonProperty(value = "LootTargetSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobLootTargetSquad lootTargetSquad;
        @JsonProperty(value = "Morph")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobMorph morph;
        /**  if you see this it means we did not account for some EA's case, so please report it */
        @JsonProperty(value = "Unknown")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobUnknown unknown;
        @JsonIgnore
        public Job get() {
            if (noJob != null) {
                return noJob;
            }
            else if (idle != null) {
                return idle;
            }
            else if (gotoJavaField != null) {
                return gotoJavaField;
            }
            else if (attackMelee != null) {
                return attackMelee;
            }
            else if (castSpell != null) {
                return castSpell;
            }
            else if (die != null) {
                return die;
            }
            else if (talk != null) {
                return talk;
            }
            else if (scriptTalk != null) {
                return scriptTalk;
            }
            else if (freeze != null) {
                return freeze;
            }
            else if (spawn != null) {
                return spawn;
            }
            else if (cheer != null) {
                return cheer;
            }
            else if (attackSquad != null) {
                return attackSquad;
            }
            else if (castSpellSquad != null) {
                return castSpellSquad;
            }
            else if (pushBack != null) {
                return pushBack;
            }
            else if (stampede != null) {
                return stampede;
            }
            else if (barrierCrush != null) {
                return barrierCrush;
            }
            else if (barrierGateToggle != null) {
                return barrierGateToggle;
            }
            else if (flameThrower != null) {
                return flameThrower;
            }
            else if (construct != null) {
                return construct;
            }
            else if (crush != null) {
                return crush;
            }
            else if (mountBarrierSquad != null) {
                return mountBarrierSquad;
            }
            else if (mountBarrier != null) {
                return mountBarrier;
            }
            else if (modeChangeSquad != null) {
                return modeChangeSquad;
            }
            else if (modeChange != null) {
                return modeChange;
            }
            else if (sacrificeSquad != null) {
                return sacrificeSquad;
            }
            else if (usePortalSquad != null) {
                return usePortalSquad;
            }
            else if (channel != null) {
                return channel;
            }
            else if (spawnSquad != null) {
                return spawnSquad;
            }
            else if (lootTargetSquad != null) {
                return lootTargetSquad;
            }
            else if (morph != null) {
                return morph;
            }
            else if (unknown != null) {
                return unknown;
            }
            else {
                throw new IllegalStateException("JobHolder doesn't contain any Job. Check implementation and API!");
            }
        }
        public JobHolder() { }
        public JobHolder(Job v) {
            Objects.requireNonNull(v, "Job must not be null");
            switch (v.getType()) {
                case JobType.NoJob:
                    this.noJob = (JobNoJob) v;
                    break;
                case JobType.Idle:
                    this.idle = (JobIdle) v;
                    break;
                case JobType.Goto:
                    this.gotoJavaField = (JobGoto) v;
                    break;
                case JobType.AttackMelee:
                    this.attackMelee = (JobAttackMelee) v;
                    break;
                case JobType.CastSpell:
                    this.castSpell = (JobCastSpell) v;
                    break;
                case JobType.Die:
                    this.die = (JobDie) v;
                    break;
                case JobType.Talk:
                    this.talk = (JobTalk) v;
                    break;
                case JobType.ScriptTalk:
                    this.scriptTalk = (JobScriptTalk) v;
                    break;
                case JobType.Freeze:
                    this.freeze = (JobFreeze) v;
                    break;
                case JobType.Spawn:
                    this.spawn = (JobSpawn) v;
                    break;
                case JobType.Cheer:
                    this.cheer = (JobCheer) v;
                    break;
                case JobType.AttackSquad:
                    this.attackSquad = (JobAttackSquad) v;
                    break;
                case JobType.CastSpellSquad:
                    this.castSpellSquad = (JobCastSpellSquad) v;
                    break;
                case JobType.PushBack:
                    this.pushBack = (JobPushBack) v;
                    break;
                case JobType.Stampede:
                    this.stampede = (JobStampede) v;
                    break;
                case JobType.BarrierCrush:
                    this.barrierCrush = (JobBarrierCrush) v;
                    break;
                case JobType.BarrierGateToggle:
                    this.barrierGateToggle = (JobBarrierGateToggle) v;
                    break;
                case JobType.FlameThrower:
                    this.flameThrower = (JobFlameThrower) v;
                    break;
                case JobType.Construct:
                    this.construct = (JobConstruct) v;
                    break;
                case JobType.Crush:
                    this.crush = (JobCrush) v;
                    break;
                case JobType.MountBarrierSquad:
                    this.mountBarrierSquad = (JobMountBarrierSquad) v;
                    break;
                case JobType.MountBarrier:
                    this.mountBarrier = (JobMountBarrier) v;
                    break;
                case JobType.ModeChangeSquad:
                    this.modeChangeSquad = (JobModeChangeSquad) v;
                    break;
                case JobType.ModeChange:
                    this.modeChange = (JobModeChange) v;
                    break;
                case JobType.SacrificeSquad:
                    this.sacrificeSquad = (JobSacrificeSquad) v;
                    break;
                case JobType.UsePortalSquad:
                    this.usePortalSquad = (JobUsePortalSquad) v;
                    break;
                case JobType.Channel:
                    this.channel = (JobChannel) v;
                    break;
                case JobType.SpawnSquad:
                    this.spawnSquad = (JobSpawnSquad) v;
                    break;
                case JobType.LootTargetSquad:
                    this.lootTargetSquad = (JobLootTargetSquad) v;
                    break;
                case JobType.Morph:
                    this.morph = (JobMorph) v;
                    break;
                case JobType.Unknown:
                    this.unknown = (JobUnknown) v;
                    break;
                default: throw new IllegalArgumentException("Unknown Job " + v.getType());
            }
        }
        public JobNoJob getNoJob() { return noJob; }
        public JobIdle getIdle() { return idle; }
        public JobGoto getGoto() { return gotoJavaField; }
        public JobAttackMelee getAttackMelee() { return attackMelee; }
        public JobCastSpell getCastSpell() { return castSpell; }
        public JobDie getDie() { return die; }
        public JobTalk getTalk() { return talk; }
        public JobScriptTalk getScriptTalk() { return scriptTalk; }
        public JobFreeze getFreeze() { return freeze; }
        public JobSpawn getSpawn() { return spawn; }
        public JobCheer getCheer() { return cheer; }
        public JobAttackSquad getAttackSquad() { return attackSquad; }
        public JobCastSpellSquad getCastSpellSquad() { return castSpellSquad; }
        public JobPushBack getPushBack() { return pushBack; }
        public JobStampede getStampede() { return stampede; }
        public JobBarrierCrush getBarrierCrush() { return barrierCrush; }
        public JobBarrierGateToggle getBarrierGateToggle() { return barrierGateToggle; }
        public JobFlameThrower getFlameThrower() { return flameThrower; }
        public JobConstruct getConstruct() { return construct; }
        public JobCrush getCrush() { return crush; }
        public JobMountBarrierSquad getMountBarrierSquad() { return mountBarrierSquad; }
        public JobMountBarrier getMountBarrier() { return mountBarrier; }
        public JobModeChangeSquad getModeChangeSquad() { return modeChangeSquad; }
        public JobModeChange getModeChange() { return modeChange; }
        public JobSacrificeSquad getSacrificeSquad() { return sacrificeSquad; }
        public JobUsePortalSquad getUsePortalSquad() { return usePortalSquad; }
        public JobChannel getChannel() { return channel; }
        public JobSpawnSquad getSpawnSquad() { return spawnSquad; }
        public JobLootTargetSquad getLootTargetSquad() { return lootTargetSquad; }
        public JobMorph getMorph() { return morph; }
        public JobUnknown getUnknown() { return unknown; }
    }

    public enum Ping {
        Attention(0),
        Attack(1),
        Defend(2),
        NeedHelp(4),
        Meet(5);

        //----------------------------------------
        public final int value;
        Ping(int value) { this.value = value; }
        @JsonValue
        public int getValue() { return value; }
        public static Optional<Upgrade> fromValue(int value) {
            return Arrays.stream(Upgrade.values())
                    .filter(u -> u.value == value)
                    .findFirst();
        }
    }

    /**  Entity on the map */
    public static class Entity {
        /**  Unique id of the entity */
        @JsonProperty(required = true)
        private EntityId id;
        /**  List of effects the entity have. */
        @JsonProperty(required = true)
        private AbilityEffect[] effects;
        /**  List of aspects entity have. */
        @JsonProperty(required = true)
        private AspectHolder[] aspects;
        /**  What is the entity doing right now */
        @JsonProperty(required = true)
        private JobHolder job;
        /**  position on the map */
        @JsonProperty(required = true)
        private Position position;
        /**  id of player that owns this entity */
        @JsonProperty(required = false)
        private EntityId player_entity_id;
        public EntityId getId() { return id; }
        public void setId(EntityId v) { this.id = v; }
        public AbilityEffect[] getEffects() { return effects; }
        public void setEffects(AbilityEffect[] v) { this.effects = v; }
        public AspectHolder[] getAspects() { return aspects; }
        public void setAspects(AspectHolder[] v) { this.aspects = v; }
        public JobHolder getJob() { return job; }
        public void setJob(JobHolder v) { this.job = v; }
        public Position getPosition() { return position; }
        public void setPosition(Position v) { this.position = v; }
        public EntityId getPlayerEntityId() { return player_entity_id; }
        public void setPlayerEntityId(EntityId v) { this.player_entity_id = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entity that = (Entity) o;
            return getId() == that.getId() && getEffects() == that.getEffects() && getAspects() == that.getAspects() && getJob() == that.getJob() && getPosition() == that.getPosition() && getPlayerEntityId() == that.getPlayerEntityId();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getId(), getEffects(), getAspects(), getJob(), getPosition(), getPlayerEntityId());
        }
        @Override
        public String toString() {
            return "{" + "id: " + id + ", effects: " + effects + ", aspects: " + aspects + ", job: " + job + ", position: " + position + ", player_entity_id: " + player_entity_id + "}";
        }
        /**  Entity on the map */
        public Entity() { }
        /**  Entity on the map */
        public Entity(EntityId id, AbilityEffect[] effects, AspectHolder[] aspects, JobHolder job, Position position, EntityId player_entity_id) {
            this.id = id;
            this.effects = effects;
            this.aspects = aspects;
            this.job = job;
            this.position = position;
            this.player_entity_id = player_entity_id;
        }
    }

    public static class Projectile {
        /**  Unique id of the entity */
        @JsonProperty(required = true)
        private EntityId id;
        /**  position on the map */
        @JsonProperty(required = true)
        private Position position;
        public EntityId getId() { return id; }
        public void setId(EntityId v) { this.id = v; }
        public Position getPosition() { return position; }
        public void setPosition(Position v) { this.position = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Projectile that = (Projectile) o;
            return getId() == that.getId() && getPosition() == that.getPosition();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getId(), getPosition());
        }
        @Override
        public String toString() {
            return "{" + "id: " + id + ", position: " + position + "}";
        }
        public Projectile() { }
        public Projectile(EntityId id, Position position) {
            this.id = id;
            this.position = position;
        }
    }

    public enum BuildState {
        /**  If you see this state, please report it with a replay, how you reached it, because it is a bug */
        _Unexpected(0),
        /**  When not build yet */
        ReadyToBuild(1),
        /**  When building (raising from the ground) */
        InProgress(2),
        /**  When functional */
        Build(3),
        /**  When being destroyed */
        Destroying(4);

        //----------------------------------------
        public final int value;
        BuildState(int value) { this.value = value; }
        @JsonValue
        public int getValue() { return value; }
        public static Optional<Upgrade> fromValue(int value) {
            return Arrays.stream(Upgrade.values())
                    .filter(u -> u.value == value)
                    .findFirst();
        }
    }

    public static class PowerSlot {
        @JsonProperty(required = true)
        private Entity entity;
        @JsonProperty(required = true)
        private int res_id;
        @JsonProperty(required = true)
        private BuildState state;
        @JsonProperty(required = true)
        private byte team;
        public Entity getEntity() { return entity; }
        public void setEntity(Entity v) { this.entity = v; }
        public int getResId() { return res_id; }
        public void setResId(int v) { this.res_id = v; }
        public BuildState getState() { return state; }
        public void setState(BuildState v) { this.state = v; }
        public byte getTeam() { return team; }
        public void setTeam(byte v) { this.team = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PowerSlot that = (PowerSlot) o;
            return getEntity() == that.getEntity() && getResId() == that.getResId() && getState() == that.getState() && getTeam() == that.getTeam();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getEntity(), getResId(), getState(), getTeam());
        }
        @Override
        public String toString() {
            return "{" + "entity: " + entity + ", res_id: " + res_id + ", state: " + state + ", team: " + team + "}";
        }
        public PowerSlot() { }
        public PowerSlot(Entity entity, int res_id, BuildState state, byte team) {
            this.entity = entity;
            this.res_id = res_id;
            this.state = state;
            this.team = team;
        }
    }

    public static class TokenSlot {
        @JsonProperty(required = true)
        private Entity entity;
        @JsonProperty(required = true)
        private int res_id;
        @JsonProperty(required = true)
        private BuildState state;
        @JsonProperty(required = true)
        private byte team;
        @JsonProperty(required = true)
        private OrbColor color;
        public Entity getEntity() { return entity; }
        public void setEntity(Entity v) { this.entity = v; }
        public int getResId() { return res_id; }
        public void setResId(int v) { this.res_id = v; }
        public BuildState getState() { return state; }
        public void setState(BuildState v) { this.state = v; }
        public byte getTeam() { return team; }
        public void setTeam(byte v) { this.team = v; }
        public OrbColor getColor() { return color; }
        public void setColor(OrbColor v) { this.color = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TokenSlot that = (TokenSlot) o;
            return getEntity() == that.getEntity() && getResId() == that.getResId() && getState() == that.getState() && getTeam() == that.getTeam() && getColor() == that.getColor();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getEntity(), getResId(), getState(), getTeam(), getColor());
        }
        @Override
        public String toString() {
            return "{" + "entity: " + entity + ", res_id: " + res_id + ", state: " + state + ", team: " + team + ", color: " + color + "}";
        }
        public TokenSlot() { }
        public TokenSlot(Entity entity, int res_id, BuildState state, byte team, OrbColor color) {
            this.entity = entity;
            this.res_id = res_id;
            this.state = state;
            this.team = team;
            this.color = color;
        }
    }

    public static class AbilityWorldObject {
        @JsonProperty(required = true)
        private Entity entity;
        public Entity getEntity() { return entity; }
        public void setEntity(Entity v) { this.entity = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AbilityWorldObject that = (AbilityWorldObject) o;
            return getEntity() == that.getEntity();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getEntity());
        }
        @Override
        public String toString() {
            return "{" + "entity: " + entity + "}";
        }
        public AbilityWorldObject() { }
        public AbilityWorldObject(Entity entity) {
            this.entity = entity;
        }
    }

    public static class Squad {
        @JsonProperty(required = true)
        private Entity entity;
        @JsonProperty(required = true)
        private CardId card_id;
        @JsonProperty(required = true)
        private SquadId res_squad_id;
        @JsonProperty(required = true)
        private float bound_power;
        @JsonProperty(required = true)
        private byte squad_size;
        /**  IDs of the figures in the squad */
        @JsonProperty(required = true)
        private EntityId[] figures;
        public Entity getEntity() { return entity; }
        public void setEntity(Entity v) { this.entity = v; }
        public CardId getCardId() { return card_id; }
        public void setCardId(CardId v) { this.card_id = v; }
        public SquadId getResSquadId() { return res_squad_id; }
        public void setResSquadId(SquadId v) { this.res_squad_id = v; }
        public float getBoundPower() { return bound_power; }
        public void setBoundPower(float v) { this.bound_power = v; }
        public byte getSquadSize() { return squad_size; }
        public void setSquadSize(byte v) { this.squad_size = v; }
        public EntityId[] getFigures() { return figures; }
        public void setFigures(EntityId[] v) { this.figures = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Squad that = (Squad) o;
            return getEntity() == that.getEntity() && getCardId() == that.getCardId() && getResSquadId() == that.getResSquadId() && getBoundPower() == that.getBoundPower() && getSquadSize() == that.getSquadSize() && getFigures() == that.getFigures();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getEntity(), getCardId(), getResSquadId(), getBoundPower(), getSquadSize(), getFigures());
        }
        @Override
        public String toString() {
            return "{" + "entity: " + entity + ", card_id: " + card_id + ", res_squad_id: " + res_squad_id + ", bound_power: " + bound_power + ", squad_size: " + squad_size + ", figures: " + figures + "}";
        }
        public Squad() { }
        public Squad(Entity entity, CardId card_id, SquadId res_squad_id, float bound_power, byte squad_size, EntityId[] figures) {
            this.entity = entity;
            this.card_id = card_id;
            this.res_squad_id = res_squad_id;
            this.bound_power = bound_power;
            this.squad_size = squad_size;
            this.figures = figures;
        }
    }

    public static class Figure {
        @JsonProperty(required = true)
        private Entity entity;
        @JsonProperty(required = true)
        private EntityId squad_id;
        @JsonProperty(required = true)
        private float current_speed;
        @JsonProperty(required = true)
        private float rotation_speed;
        @JsonProperty(required = true)
        private byte unit_size;
        @JsonProperty(required = true)
        private byte move_mode;
        public Entity getEntity() { return entity; }
        public void setEntity(Entity v) { this.entity = v; }
        public EntityId getSquadId() { return squad_id; }
        public void setSquadId(EntityId v) { this.squad_id = v; }
        public float getCurrentSpeed() { return current_speed; }
        public void setCurrentSpeed(float v) { this.current_speed = v; }
        public float getRotationSpeed() { return rotation_speed; }
        public void setRotationSpeed(float v) { this.rotation_speed = v; }
        public byte getUnitSize() { return unit_size; }
        public void setUnitSize(byte v) { this.unit_size = v; }
        public byte getMoveMode() { return move_mode; }
        public void setMoveMode(byte v) { this.move_mode = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Figure that = (Figure) o;
            return getEntity() == that.getEntity() && getSquadId() == that.getSquadId() && getCurrentSpeed() == that.getCurrentSpeed() && getRotationSpeed() == that.getRotationSpeed() && getUnitSize() == that.getUnitSize() && getMoveMode() == that.getMoveMode();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getEntity(), getSquadId(), getCurrentSpeed(), getRotationSpeed(), getUnitSize(), getMoveMode());
        }
        @Override
        public String toString() {
            return "{" + "entity: " + entity + ", squad_id: " + squad_id + ", current_speed: " + current_speed + ", rotation_speed: " + rotation_speed + ", unit_size: " + unit_size + ", move_mode: " + move_mode + "}";
        }
        public Figure() { }
        public Figure(Entity entity, EntityId squad_id, float current_speed, float rotation_speed, byte unit_size, byte move_mode) {
            this.entity = entity;
            this.squad_id = squad_id;
            this.current_speed = current_speed;
            this.rotation_speed = rotation_speed;
            this.unit_size = unit_size;
            this.move_mode = move_mode;
        }
    }

    public static class Building {
        @JsonProperty(required = true)
        private Entity entity;
        @JsonProperty(required = true)
        private BuildingId building_id;
        @JsonProperty(required = true)
        private CardId card_id;
        @JsonProperty(required = true)
        private float power_cost;
        public Entity getEntity() { return entity; }
        public void setEntity(Entity v) { this.entity = v; }
        public BuildingId getBuildingId() { return building_id; }
        public void setBuildingId(BuildingId v) { this.building_id = v; }
        public CardId getCardId() { return card_id; }
        public void setCardId(CardId v) { this.card_id = v; }
        public float getPowerCost() { return power_cost; }
        public void setPowerCost(float v) { this.power_cost = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Building that = (Building) o;
            return getEntity() == that.getEntity() && getBuildingId() == that.getBuildingId() && getCardId() == that.getCardId() && getPowerCost() == that.getPowerCost();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getEntity(), getBuildingId(), getCardId(), getPowerCost());
        }
        @Override
        public String toString() {
            return "{" + "entity: " + entity + ", building_id: " + building_id + ", card_id: " + card_id + ", power_cost: " + power_cost + "}";
        }
        public Building() { }
        public Building(Entity entity, BuildingId building_id, CardId card_id, float power_cost) {
            this.entity = entity;
            this.building_id = building_id;
            this.card_id = card_id;
            this.power_cost = power_cost;
        }
    }

    public static class BarrierSet {
        @JsonProperty(required = true)
        private Entity entity;
        public Entity getEntity() { return entity; }
        public void setEntity(Entity v) { this.entity = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BarrierSet that = (BarrierSet) o;
            return getEntity() == that.getEntity();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getEntity());
        }
        @Override
        public String toString() {
            return "{" + "entity: " + entity + "}";
        }
        public BarrierSet() { }
        public BarrierSet(Entity entity) {
            this.entity = entity;
        }
    }

    public static class BarrierModule {
        @JsonProperty(required = true)
        private Entity entity;
        @JsonProperty(required = true)
        private byte team;
        @JsonProperty(required = true)
        private EntityId set;
        @JsonProperty(required = true)
        private int state;
        @JsonProperty(required = true)
        private byte slots;
        @JsonProperty(required = true)
        private byte free_slots;
        @JsonProperty(required = true)
        private boolean walkable;
        public Entity getEntity() { return entity; }
        public void setEntity(Entity v) { this.entity = v; }
        public byte getTeam() { return team; }
        public void setTeam(byte v) { this.team = v; }
        public EntityId getSet() { return set; }
        public void setSet(EntityId v) { this.set = v; }
        public int getState() { return state; }
        public void setState(int v) { this.state = v; }
        public byte getSlots() { return slots; }
        public void setSlots(byte v) { this.slots = v; }
        public byte getFreeSlots() { return free_slots; }
        public void setFreeSlots(byte v) { this.free_slots = v; }
        public boolean getWalkable() { return walkable; }
        public void setWalkable(boolean v) { this.walkable = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BarrierModule that = (BarrierModule) o;
            return getEntity() == that.getEntity() && getTeam() == that.getTeam() && getSet() == that.getSet() && getState() == that.getState() && getSlots() == that.getSlots() && getFreeSlots() == that.getFreeSlots() && getWalkable() == that.getWalkable();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getEntity(), getTeam(), getSet(), getState(), getSlots(), getFreeSlots(), getWalkable());
        }
        @Override
        public String toString() {
            return "{" + "entity: " + entity + ", team: " + team + ", set: " + set + ", state: " + state + ", slots: " + slots + ", free_slots: " + free_slots + ", walkable: " + walkable + "}";
        }
        public BarrierModule() { }
        public BarrierModule(Entity entity, byte team, EntityId set, int state, byte slots, byte free_slots, boolean walkable) {
            this.entity = entity;
            this.team = team;
            this.set = set;
            this.state = state;
            this.slots = slots;
            this.free_slots = free_slots;
            this.walkable = walkable;
        }
    }

    public enum CommandType {
        BuildHouse,
        CastSpellGod,
        CastSpellGodMulti,
        ProduceSquad,
        ProduceSquadOnBarrier,
        CastSpellEntity,
        BarrierGateToggle,
        BarrierBuild,
        BarrierRepair,
        BarrierCancelRepair,
        RepairBuilding,
        CancelRepairBuilding,
        GroupAttack,
        GroupEnterWall,
        GroupExitWall,
        GroupGoto,
        GroupHoldPosition,
        GroupStopJob,
        ModeChange,
        PowerSlotBuild,
        TokenSlotBuild,
        GroupKillEntity,
        GroupSacrifice,
        PortalDefineExitPoint,
        PortalRemoveExitPoint,
        TunnelMakeExitPoint,
        Ping,
        Surrender,
        WhisperToMaster;
    }
    /** Marker fo all Command implementations */
    public interface Command extends MultiType<CommandType> {}
    /**  Play card of building type. */
    public static final class CommandBuildHouse implements  Command {
        @JsonProperty(required = true)
        private byte card_position;
        @JsonProperty(required = true)
        private Position2D xy;
        @JsonProperty(required = true)
        private float angle;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.BuildHouse; }
        public byte getCardPosition() { return card_position; }
        public void setCardPosition(byte v) { this.card_position = v; }
        public Position2D getXy() { return xy; }
        public void setXy(Position2D v) { this.xy = v; }
        public float getAngle() { return angle; }
        public void setAngle(float v) { this.angle = v; }
        /**  Play card of building type. */
        public CommandBuildHouse() { }
        /**  Play card of building type. */
        public CommandBuildHouse(byte card_position, Position2D xy, float angle) {
            this.card_position = card_position;
            this.xy = xy;
            this.angle = angle;
        }
    }
    /**  Play card of Spell type. (single target) */
    public static final class CommandCastSpellGod implements  Command {
        @JsonProperty(required = true)
        private byte card_position;
        @JsonProperty(required = true)
        private SingleTargetHolder target;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.CastSpellGod; }
        public byte getCardPosition() { return card_position; }
        public void setCardPosition(byte v) { this.card_position = v; }
        public SingleTargetHolder getTarget() { return target; }
        public void setTarget(SingleTargetHolder v) { this.target = v; }
        /**  Play card of Spell type. (single target) */
        public CommandCastSpellGod() { }
        /**  Play card of Spell type. (single target) */
        public CommandCastSpellGod(byte card_position, SingleTargetHolder target) {
            this.card_position = card_position;
            this.target = target;
        }
    }
    /**  Play card of Spell type. (line target) */
    public static final class CommandCastSpellGodMulti implements  Command {
        @JsonProperty(required = true)
        private byte card_position;
        @JsonProperty(required = true)
        private Position2D xy1;
        @JsonProperty(required = true)
        private Position2D xy2;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.CastSpellGodMulti; }
        public byte getCardPosition() { return card_position; }
        public void setCardPosition(byte v) { this.card_position = v; }
        public Position2D getXy1() { return xy1; }
        public void setXy1(Position2D v) { this.xy1 = v; }
        public Position2D getXy2() { return xy2; }
        public void setXy2(Position2D v) { this.xy2 = v; }
        /**  Play card of Spell type. (line target) */
        public CommandCastSpellGodMulti() { }
        /**  Play card of Spell type. (line target) */
        public CommandCastSpellGodMulti(byte card_position, Position2D xy1, Position2D xy2) {
            this.card_position = card_position;
            this.xy1 = xy1;
            this.xy2 = xy2;
        }
    }
    /**  Play card of squad type (on ground) */
    public static final class CommandProduceSquad implements  Command {
        @JsonProperty(required = true)
        private byte card_position;
        @JsonProperty(required = true)
        private Position2D xy;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.ProduceSquad; }
        public byte getCardPosition() { return card_position; }
        public void setCardPosition(byte v) { this.card_position = v; }
        public Position2D getXy() { return xy; }
        public void setXy(Position2D v) { this.xy = v; }
        /**  Play card of squad type (on ground) */
        public CommandProduceSquad() { }
        /**  Play card of squad type (on ground) */
        public CommandProduceSquad(byte card_position, Position2D xy) {
            this.card_position = card_position;
            this.xy = xy;
        }
    }
    /**  Play card of squad type (on barrier) */
    public static final class CommandProduceSquadOnBarrier implements  Command {
        @JsonProperty(required = true)
        private byte card_position;
        @JsonProperty(required = true)
        private Position2D xy;
        @JsonProperty(required = true)
        private EntityId barrier_to_mount;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.ProduceSquadOnBarrier; }
        public byte getCardPosition() { return card_position; }
        public void setCardPosition(byte v) { this.card_position = v; }
        public Position2D getXy() { return xy; }
        public void setXy(Position2D v) { this.xy = v; }
        public EntityId getBarrierToMount() { return barrier_to_mount; }
        public void setBarrierToMount(EntityId v) { this.barrier_to_mount = v; }
        /**  Play card of squad type (on barrier) */
        public CommandProduceSquadOnBarrier() { }
        /**  Play card of squad type (on barrier) */
        public CommandProduceSquadOnBarrier(byte card_position, Position2D xy, EntityId barrier_to_mount) {
            this.card_position = card_position;
            this.xy = xy;
            this.barrier_to_mount = barrier_to_mount;
        }
    }
    /**  Activates spell or ability on entity. */
    public static final class CommandCastSpellEntity implements  Command {
        @JsonProperty(required = true)
        private EntityId entity;
        @JsonProperty(required = true)
        private SpellId spell;
        @JsonProperty(required = true)
        private SingleTargetHolder target;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.CastSpellEntity; }
        public EntityId getEntity() { return entity; }
        public void setEntity(EntityId v) { this.entity = v; }
        public SpellId getSpell() { return spell; }
        public void setSpell(SpellId v) { this.spell = v; }
        public SingleTargetHolder getTarget() { return target; }
        public void setTarget(SingleTargetHolder v) { this.target = v; }
        /**  Activates spell or ability on entity. */
        public CommandCastSpellEntity() { }
        /**  Activates spell or ability on entity. */
        public CommandCastSpellEntity(EntityId entity, SpellId spell, SingleTargetHolder target) {
            this.entity = entity;
            this.spell = spell;
            this.target = target;
        }
    }
    /**  Opens or closes gate. */
    public static final class CommandBarrierGateToggle implements  Command {
        @JsonProperty(required = true)
        private EntityId barrier_id;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.BarrierGateToggle; }
        public EntityId getBarrierId() { return barrier_id; }
        public void setBarrierId(EntityId v) { this.barrier_id = v; }
        /**  Opens or closes gate. */
        public CommandBarrierGateToggle() { }
        /**  Opens or closes gate. */
        public CommandBarrierGateToggle(EntityId barrier_id) {
            this.barrier_id = barrier_id;
        }
    }
    /**  Build barrier. (same as BarrierRepair if not inverted) */
    public static final class CommandBarrierBuild implements  Command {
        @JsonProperty(required = true)
        private EntityId barrier_id;
        @JsonProperty(required = true)
        private boolean inverted_direction;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.BarrierBuild; }
        public EntityId getBarrierId() { return barrier_id; }
        public void setBarrierId(EntityId v) { this.barrier_id = v; }
        public boolean getInvertedDirection() { return inverted_direction; }
        public void setInvertedDirection(boolean v) { this.inverted_direction = v; }
        /**  Build barrier. (same as BarrierRepair if not inverted) */
        public CommandBarrierBuild() { }
        /**  Build barrier. (same as BarrierRepair if not inverted) */
        public CommandBarrierBuild(EntityId barrier_id, boolean inverted_direction) {
            this.barrier_id = barrier_id;
            this.inverted_direction = inverted_direction;
        }
    }
    /**  Repair barrier. */
    public static final class CommandBarrierRepair implements  Command {
        @JsonProperty(required = true)
        private EntityId barrier_id;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.BarrierRepair; }
        public EntityId getBarrierId() { return barrier_id; }
        public void setBarrierId(EntityId v) { this.barrier_id = v; }
        /**  Repair barrier. */
        public CommandBarrierRepair() { }
        /**  Repair barrier. */
        public CommandBarrierRepair(EntityId barrier_id) {
            this.barrier_id = barrier_id;
        }
    }
    public static final class CommandBarrierCancelRepair implements  Command {
        @JsonProperty(required = true)
        private EntityId barrier_id;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.BarrierCancelRepair; }
        public EntityId getBarrierId() { return barrier_id; }
        public void setBarrierId(EntityId v) { this.barrier_id = v; }
        public CommandBarrierCancelRepair() { }
        public CommandBarrierCancelRepair(EntityId barrier_id) {
            this.barrier_id = barrier_id;
        }
    }
    public static final class CommandRepairBuilding implements  Command {
        @JsonProperty(required = true)
        private EntityId building_id;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.RepairBuilding; }
        public EntityId getBuildingId() { return building_id; }
        public void setBuildingId(EntityId v) { this.building_id = v; }
        public CommandRepairBuilding() { }
        public CommandRepairBuilding(EntityId building_id) {
            this.building_id = building_id;
        }
    }
    public static final class CommandCancelRepairBuilding implements  Command {
        @JsonProperty(required = true)
        private EntityId building_id;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.CancelRepairBuilding; }
        public EntityId getBuildingId() { return building_id; }
        public void setBuildingId(EntityId v) { this.building_id = v; }
        public CommandCancelRepairBuilding() { }
        public CommandCancelRepairBuilding(EntityId building_id) {
            this.building_id = building_id;
        }
    }
    public static final class CommandGroupAttack implements  Command {
        @JsonProperty(required = true)
        private EntityId[] squads;
        @JsonProperty(required = true)
        private EntityId target_entity_id;
        @JsonProperty(required = true)
        private boolean force_attack;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.GroupAttack; }
        public EntityId[] getSquads() { return squads; }
        public void setSquads(EntityId[] v) { this.squads = v; }
        public EntityId getTargetEntityId() { return target_entity_id; }
        public void setTargetEntityId(EntityId v) { this.target_entity_id = v; }
        public boolean getForceAttack() { return force_attack; }
        public void setForceAttack(boolean v) { this.force_attack = v; }
        public CommandGroupAttack() { }
        public CommandGroupAttack(EntityId[] squads, EntityId target_entity_id, boolean force_attack) {
            this.squads = squads;
            this.target_entity_id = target_entity_id;
            this.force_attack = force_attack;
        }
    }
    public static final class CommandGroupEnterWall implements  Command {
        @JsonProperty(required = true)
        private EntityId[] squads;
        @JsonProperty(required = true)
        private EntityId barrier_id;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.GroupEnterWall; }
        public EntityId[] getSquads() { return squads; }
        public void setSquads(EntityId[] v) { this.squads = v; }
        public EntityId getBarrierId() { return barrier_id; }
        public void setBarrierId(EntityId v) { this.barrier_id = v; }
        public CommandGroupEnterWall() { }
        public CommandGroupEnterWall(EntityId[] squads, EntityId barrier_id) {
            this.squads = squads;
            this.barrier_id = barrier_id;
        }
    }
    public static final class CommandGroupExitWall implements  Command {
        @JsonProperty(required = true)
        private EntityId[] squads;
        @JsonProperty(required = true)
        private EntityId barrier_id;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.GroupExitWall; }
        public EntityId[] getSquads() { return squads; }
        public void setSquads(EntityId[] v) { this.squads = v; }
        public EntityId getBarrierId() { return barrier_id; }
        public void setBarrierId(EntityId v) { this.barrier_id = v; }
        public CommandGroupExitWall() { }
        public CommandGroupExitWall(EntityId[] squads, EntityId barrier_id) {
            this.squads = squads;
            this.barrier_id = barrier_id;
        }
    }
    public static final class CommandGroupGoto implements  Command {
        @JsonProperty(required = true)
        private EntityId[] squads;
        @JsonProperty(required = true)
        private Position2D[] positions;
        @JsonProperty(required = true)
        private WalkMode walk_mode;
        @JsonProperty(required = true)
        private float orientation;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.GroupGoto; }
        public EntityId[] getSquads() { return squads; }
        public void setSquads(EntityId[] v) { this.squads = v; }
        public Position2D[] getPositions() { return positions; }
        public void setPositions(Position2D[] v) { this.positions = v; }
        public WalkMode getWalkMode() { return walk_mode; }
        public void setWalkMode(WalkMode v) { this.walk_mode = v; }
        public float getOrientation() { return orientation; }
        public void setOrientation(float v) { this.orientation = v; }
        public CommandGroupGoto() { }
        public CommandGroupGoto(EntityId[] squads, Position2D[] positions, WalkMode walk_mode, float orientation) {
            this.squads = squads;
            this.positions = positions;
            this.walk_mode = walk_mode;
            this.orientation = orientation;
        }
    }
    public static final class CommandGroupHoldPosition implements  Command {
        @JsonProperty(required = true)
        private EntityId[] squads;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.GroupHoldPosition; }
        public EntityId[] getSquads() { return squads; }
        public void setSquads(EntityId[] v) { this.squads = v; }
        public CommandGroupHoldPosition() { }
        public CommandGroupHoldPosition(EntityId[] squads) {
            this.squads = squads;
        }
    }
    public static final class CommandGroupStopJob implements  Command {
        @JsonProperty(required = true)
        private EntityId[] squads;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.GroupStopJob; }
        public EntityId[] getSquads() { return squads; }
        public void setSquads(EntityId[] v) { this.squads = v; }
        public CommandGroupStopJob() { }
        public CommandGroupStopJob(EntityId[] squads) {
            this.squads = squads;
        }
    }
    public static final class CommandModeChange implements  Command {
        @JsonProperty(required = true)
        private EntityId entity_id;
        @JsonProperty(required = true)
        private ModeId new_mode_id;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.ModeChange; }
        public EntityId getEntityId() { return entity_id; }
        public void setEntityId(EntityId v) { this.entity_id = v; }
        public ModeId getNewModeId() { return new_mode_id; }
        public void setNewModeId(ModeId v) { this.new_mode_id = v; }
        public CommandModeChange() { }
        public CommandModeChange(EntityId entity_id, ModeId new_mode_id) {
            this.entity_id = entity_id;
            this.new_mode_id = new_mode_id;
        }
    }
    public static final class CommandPowerSlotBuild implements  Command {
        @JsonProperty(required = true)
        private EntityId slot_id;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.PowerSlotBuild; }
        public EntityId getSlotId() { return slot_id; }
        public void setSlotId(EntityId v) { this.slot_id = v; }
        public CommandPowerSlotBuild() { }
        public CommandPowerSlotBuild(EntityId slot_id) {
            this.slot_id = slot_id;
        }
    }
    public static final class CommandTokenSlotBuild implements  Command {
        @JsonProperty(required = true)
        private EntityId slot_id;
        @JsonProperty(required = true)
        private CreateOrbColor color;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.TokenSlotBuild; }
        public EntityId getSlotId() { return slot_id; }
        public void setSlotId(EntityId v) { this.slot_id = v; }
        public CreateOrbColor getColor() { return color; }
        public void setColor(CreateOrbColor v) { this.color = v; }
        public CommandTokenSlotBuild() { }
        public CommandTokenSlotBuild(EntityId slot_id, CreateOrbColor color) {
            this.slot_id = slot_id;
            this.color = color;
        }
    }
    public static final class CommandGroupKillEntity implements  Command {
        @JsonProperty(required = true)
        private EntityId[] entities;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.GroupKillEntity; }
        public EntityId[] getEntities() { return entities; }
        public void setEntities(EntityId[] v) { this.entities = v; }
        public CommandGroupKillEntity() { }
        public CommandGroupKillEntity(EntityId[] entities) {
            this.entities = entities;
        }
    }
    public static final class CommandGroupSacrifice implements  Command {
        @JsonProperty(required = true)
        private EntityId[] squads;
        @JsonProperty(required = true)
        private EntityId target;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.GroupSacrifice; }
        public EntityId[] getSquads() { return squads; }
        public void setSquads(EntityId[] v) { this.squads = v; }
        public EntityId getTarget() { return target; }
        public void setTarget(EntityId v) { this.target = v; }
        public CommandGroupSacrifice() { }
        public CommandGroupSacrifice(EntityId[] squads, EntityId target) {
            this.squads = squads;
            this.target = target;
        }
    }
    public static final class CommandPortalDefineExitPoint implements  Command {
        @JsonProperty(required = true)
        private EntityId portal;
        @JsonProperty(required = true)
        private Position2D xy;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.PortalDefineExitPoint; }
        public EntityId getPortal() { return portal; }
        public void setPortal(EntityId v) { this.portal = v; }
        public Position2D getXy() { return xy; }
        public void setXy(Position2D v) { this.xy = v; }
        public CommandPortalDefineExitPoint() { }
        public CommandPortalDefineExitPoint(EntityId portal, Position2D xy) {
            this.portal = portal;
            this.xy = xy;
        }
    }
    public static final class CommandPortalRemoveExitPoint implements  Command {
        @JsonProperty(required = true)
        private EntityId portal;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.PortalRemoveExitPoint; }
        public EntityId getPortal() { return portal; }
        public void setPortal(EntityId v) { this.portal = v; }
        public CommandPortalRemoveExitPoint() { }
        public CommandPortalRemoveExitPoint(EntityId portal) {
            this.portal = portal;
        }
    }
    public static final class CommandTunnelMakeExitPoint implements  Command {
        @JsonProperty(required = true)
        private EntityId portal;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.TunnelMakeExitPoint; }
        public EntityId getPortal() { return portal; }
        public void setPortal(EntityId v) { this.portal = v; }
        public CommandTunnelMakeExitPoint() { }
        public CommandTunnelMakeExitPoint(EntityId portal) {
            this.portal = portal;
        }
    }
    public static final class CommandPing implements  Command {
        @JsonProperty(required = true)
        private Position2D xy;
        @JsonProperty(required = true)
        private Ping ping;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.Ping; }
        public Position2D getXy() { return xy; }
        public void setXy(Position2D v) { this.xy = v; }
        public Ping getPing() { return ping; }
        public void setPing(Ping v) { this.ping = v; }
        public CommandPing() { }
        public CommandPing(Position2D xy, Ping ping) {
            this.xy = xy;
            this.ping = ping;
        }
    }
    public static final class CommandSurrender implements  Command {
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.Surrender; }
        public CommandSurrender() { }
    }
    public static final class CommandWhisperToMaster implements  Command {
        @JsonProperty(required = true)
        private String text;
        @Override
        @JsonIgnore
        public CommandType getType() { return CommandType.WhisperToMaster; }
        public String getText() { return text; }
        public void setText(String v) { this.text = v; }
        public CommandWhisperToMaster() { }
        public CommandWhisperToMaster(String text) {
            this.text = text;
        }
    }
    /**  All the different command bot can issue.
     *  For spectating bots all commands except Ping and WhisperToMaster are ignored
     */
    public static class CommandHolder {
        /**  Play card of building type. */
        @JsonProperty(value = "BuildHouse")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandBuildHouse buildHouse;
        /**  Play card of Spell type. (single target) */
        @JsonProperty(value = "CastSpellGod")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandCastSpellGod castSpellGod;
        /**  Play card of Spell type. (line target) */
        @JsonProperty(value = "CastSpellGodMulti")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandCastSpellGodMulti castSpellGodMulti;
        /**  Play card of squad type (on ground) */
        @JsonProperty(value = "ProduceSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandProduceSquad produceSquad;
        /**  Play card of squad type (on barrier) */
        @JsonProperty(value = "ProduceSquadOnBarrier")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandProduceSquadOnBarrier produceSquadOnBarrier;
        /**  Activates spell or ability on entity. */
        @JsonProperty(value = "CastSpellEntity")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandCastSpellEntity castSpellEntity;
        /**  Opens or closes gate. */
        @JsonProperty(value = "BarrierGateToggle")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandBarrierGateToggle barrierGateToggle;
        /**  Build barrier. (same as BarrierRepair if not inverted) */
        @JsonProperty(value = "BarrierBuild")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandBarrierBuild barrierBuild;
        /**  Repair barrier. */
        @JsonProperty(value = "BarrierRepair")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandBarrierRepair barrierRepair;
        @JsonProperty(value = "BarrierCancelRepair")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandBarrierCancelRepair barrierCancelRepair;
        @JsonProperty(value = "RepairBuilding")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRepairBuilding repairBuilding;
        @JsonProperty(value = "CancelRepairBuilding")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandCancelRepairBuilding cancelRepairBuilding;
        @JsonProperty(value = "GroupAttack")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandGroupAttack groupAttack;
        @JsonProperty(value = "GroupEnterWall")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandGroupEnterWall groupEnterWall;
        @JsonProperty(value = "GroupExitWall")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandGroupExitWall groupExitWall;
        @JsonProperty(value = "GroupGoto")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandGroupGoto groupGoto;
        @JsonProperty(value = "GroupHoldPosition")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandGroupHoldPosition groupHoldPosition;
        @JsonProperty(value = "GroupStopJob")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandGroupStopJob groupStopJob;
        @JsonProperty(value = "ModeChange")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandModeChange modeChange;
        @JsonProperty(value = "PowerSlotBuild")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandPowerSlotBuild powerSlotBuild;
        @JsonProperty(value = "TokenSlotBuild")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandTokenSlotBuild tokenSlotBuild;
        @JsonProperty(value = "GroupKillEntity")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandGroupKillEntity groupKillEntity;
        @JsonProperty(value = "GroupSacrifice")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandGroupSacrifice groupSacrifice;
        @JsonProperty(value = "PortalDefineExitPoint")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandPortalDefineExitPoint portalDefineExitPoint;
        @JsonProperty(value = "PortalRemoveExitPoint")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandPortalRemoveExitPoint portalRemoveExitPoint;
        @JsonProperty(value = "TunnelMakeExitPoint")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandTunnelMakeExitPoint tunnelMakeExitPoint;
        @JsonProperty(value = "Ping")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandPing ping;
        @JsonProperty(value = "Surrender")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandSurrender surrender;
        @JsonProperty(value = "WhisperToMaster")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandWhisperToMaster whisperToMaster;
        @JsonIgnore
        public Command get() {
            if (buildHouse != null) {
                return buildHouse;
            }
            else if (castSpellGod != null) {
                return castSpellGod;
            }
            else if (castSpellGodMulti != null) {
                return castSpellGodMulti;
            }
            else if (produceSquad != null) {
                return produceSquad;
            }
            else if (produceSquadOnBarrier != null) {
                return produceSquadOnBarrier;
            }
            else if (castSpellEntity != null) {
                return castSpellEntity;
            }
            else if (barrierGateToggle != null) {
                return barrierGateToggle;
            }
            else if (barrierBuild != null) {
                return barrierBuild;
            }
            else if (barrierRepair != null) {
                return barrierRepair;
            }
            else if (barrierCancelRepair != null) {
                return barrierCancelRepair;
            }
            else if (repairBuilding != null) {
                return repairBuilding;
            }
            else if (cancelRepairBuilding != null) {
                return cancelRepairBuilding;
            }
            else if (groupAttack != null) {
                return groupAttack;
            }
            else if (groupEnterWall != null) {
                return groupEnterWall;
            }
            else if (groupExitWall != null) {
                return groupExitWall;
            }
            else if (groupGoto != null) {
                return groupGoto;
            }
            else if (groupHoldPosition != null) {
                return groupHoldPosition;
            }
            else if (groupStopJob != null) {
                return groupStopJob;
            }
            else if (modeChange != null) {
                return modeChange;
            }
            else if (powerSlotBuild != null) {
                return powerSlotBuild;
            }
            else if (tokenSlotBuild != null) {
                return tokenSlotBuild;
            }
            else if (groupKillEntity != null) {
                return groupKillEntity;
            }
            else if (groupSacrifice != null) {
                return groupSacrifice;
            }
            else if (portalDefineExitPoint != null) {
                return portalDefineExitPoint;
            }
            else if (portalRemoveExitPoint != null) {
                return portalRemoveExitPoint;
            }
            else if (tunnelMakeExitPoint != null) {
                return tunnelMakeExitPoint;
            }
            else if (ping != null) {
                return ping;
            }
            else if (surrender != null) {
                return surrender;
            }
            else if (whisperToMaster != null) {
                return whisperToMaster;
            }
            else {
                throw new IllegalStateException("CommandHolder doesn't contain any Command. Check implementation and API!");
            }
        }
        public CommandHolder() { }
        public CommandHolder(Command v) {
            Objects.requireNonNull(v, "Command must not be null");
            switch (v.getType()) {
                case CommandType.BuildHouse:
                    this.buildHouse = (CommandBuildHouse) v;
                    break;
                case CommandType.CastSpellGod:
                    this.castSpellGod = (CommandCastSpellGod) v;
                    break;
                case CommandType.CastSpellGodMulti:
                    this.castSpellGodMulti = (CommandCastSpellGodMulti) v;
                    break;
                case CommandType.ProduceSquad:
                    this.produceSquad = (CommandProduceSquad) v;
                    break;
                case CommandType.ProduceSquadOnBarrier:
                    this.produceSquadOnBarrier = (CommandProduceSquadOnBarrier) v;
                    break;
                case CommandType.CastSpellEntity:
                    this.castSpellEntity = (CommandCastSpellEntity) v;
                    break;
                case CommandType.BarrierGateToggle:
                    this.barrierGateToggle = (CommandBarrierGateToggle) v;
                    break;
                case CommandType.BarrierBuild:
                    this.barrierBuild = (CommandBarrierBuild) v;
                    break;
                case CommandType.BarrierRepair:
                    this.barrierRepair = (CommandBarrierRepair) v;
                    break;
                case CommandType.BarrierCancelRepair:
                    this.barrierCancelRepair = (CommandBarrierCancelRepair) v;
                    break;
                case CommandType.RepairBuilding:
                    this.repairBuilding = (CommandRepairBuilding) v;
                    break;
                case CommandType.CancelRepairBuilding:
                    this.cancelRepairBuilding = (CommandCancelRepairBuilding) v;
                    break;
                case CommandType.GroupAttack:
                    this.groupAttack = (CommandGroupAttack) v;
                    break;
                case CommandType.GroupEnterWall:
                    this.groupEnterWall = (CommandGroupEnterWall) v;
                    break;
                case CommandType.GroupExitWall:
                    this.groupExitWall = (CommandGroupExitWall) v;
                    break;
                case CommandType.GroupGoto:
                    this.groupGoto = (CommandGroupGoto) v;
                    break;
                case CommandType.GroupHoldPosition:
                    this.groupHoldPosition = (CommandGroupHoldPosition) v;
                    break;
                case CommandType.GroupStopJob:
                    this.groupStopJob = (CommandGroupStopJob) v;
                    break;
                case CommandType.ModeChange:
                    this.modeChange = (CommandModeChange) v;
                    break;
                case CommandType.PowerSlotBuild:
                    this.powerSlotBuild = (CommandPowerSlotBuild) v;
                    break;
                case CommandType.TokenSlotBuild:
                    this.tokenSlotBuild = (CommandTokenSlotBuild) v;
                    break;
                case CommandType.GroupKillEntity:
                    this.groupKillEntity = (CommandGroupKillEntity) v;
                    break;
                case CommandType.GroupSacrifice:
                    this.groupSacrifice = (CommandGroupSacrifice) v;
                    break;
                case CommandType.PortalDefineExitPoint:
                    this.portalDefineExitPoint = (CommandPortalDefineExitPoint) v;
                    break;
                case CommandType.PortalRemoveExitPoint:
                    this.portalRemoveExitPoint = (CommandPortalRemoveExitPoint) v;
                    break;
                case CommandType.TunnelMakeExitPoint:
                    this.tunnelMakeExitPoint = (CommandTunnelMakeExitPoint) v;
                    break;
                case CommandType.Ping:
                    this.ping = (CommandPing) v;
                    break;
                case CommandType.Surrender:
                    this.surrender = (CommandSurrender) v;
                    break;
                case CommandType.WhisperToMaster:
                    this.whisperToMaster = (CommandWhisperToMaster) v;
                    break;
                default: throw new IllegalArgumentException("Unknown Command " + v.getType());
            }
        }
        public CommandBuildHouse getBuildHouse() { return buildHouse; }
        public CommandCastSpellGod getCastSpellGod() { return castSpellGod; }
        public CommandCastSpellGodMulti getCastSpellGodMulti() { return castSpellGodMulti; }
        public CommandProduceSquad getProduceSquad() { return produceSquad; }
        public CommandProduceSquadOnBarrier getProduceSquadOnBarrier() { return produceSquadOnBarrier; }
        public CommandCastSpellEntity getCastSpellEntity() { return castSpellEntity; }
        public CommandBarrierGateToggle getBarrierGateToggle() { return barrierGateToggle; }
        public CommandBarrierBuild getBarrierBuild() { return barrierBuild; }
        public CommandBarrierRepair getBarrierRepair() { return barrierRepair; }
        public CommandBarrierCancelRepair getBarrierCancelRepair() { return barrierCancelRepair; }
        public CommandRepairBuilding getRepairBuilding() { return repairBuilding; }
        public CommandCancelRepairBuilding getCancelRepairBuilding() { return cancelRepairBuilding; }
        public CommandGroupAttack getGroupAttack() { return groupAttack; }
        public CommandGroupEnterWall getGroupEnterWall() { return groupEnterWall; }
        public CommandGroupExitWall getGroupExitWall() { return groupExitWall; }
        public CommandGroupGoto getGroupGoto() { return groupGoto; }
        public CommandGroupHoldPosition getGroupHoldPosition() { return groupHoldPosition; }
        public CommandGroupStopJob getGroupStopJob() { return groupStopJob; }
        public CommandModeChange getModeChange() { return modeChange; }
        public CommandPowerSlotBuild getPowerSlotBuild() { return powerSlotBuild; }
        public CommandTokenSlotBuild getTokenSlotBuild() { return tokenSlotBuild; }
        public CommandGroupKillEntity getGroupKillEntity() { return groupKillEntity; }
        public CommandGroupSacrifice getGroupSacrifice() { return groupSacrifice; }
        public CommandPortalDefineExitPoint getPortalDefineExitPoint() { return portalDefineExitPoint; }
        public CommandPortalRemoveExitPoint getPortalRemoveExitPoint() { return portalRemoveExitPoint; }
        public CommandTunnelMakeExitPoint getTunnelMakeExitPoint() { return tunnelMakeExitPoint; }
        public CommandPing getPing() { return ping; }
        public CommandSurrender getSurrender() { return surrender; }
        public CommandWhisperToMaster getWhisperToMaster() { return whisperToMaster; }
    }

    /**  Command that happen. */
    public static class PlayerCommand {
        @JsonProperty(required = true)
        private EntityId player;
        @JsonProperty(required = true)
        private CommandHolder command;
        public EntityId getPlayer() { return player; }
        public void setPlayer(EntityId v) { this.player = v; }
        public CommandHolder getCommand() { return command; }
        public void setCommand(CommandHolder v) { this.command = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlayerCommand that = (PlayerCommand) o;
            return getPlayer() == that.getPlayer() && getCommand() == that.getCommand();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getPlayer(), getCommand());
        }
        @Override
        public String toString() {
            return "{" + "player: " + player + ", command: " + command + "}";
        }
        /**  Command that happen. */
        public PlayerCommand() { }
        /**  Command that happen. */
        public PlayerCommand(EntityId player, CommandHolder command) {
            this.player = player;
            this.command = command;
        }
    }

    /**  Kind of Bit flags
     *  CanPlay = 0,
     *  PlayerNotFound = 0x1,
     *  CardOrSpellDoesNotExist = 0x2,
     *  DoesNotHaveEnoughPower = 0x10,
     *  InvalidPosition = 0x20, // too close to (0,y), or (x,0)
     *  CardCondition = 0x80,
     *  ConditionPreventCardPlay = 0x100, // searched in up to 50m radius?
     *  DoesNotHaveThatCard = 0x200,
     *  DoesNotHaveEnoughOrbs = 0x400,
     *  NotEnoughPopulation = 0x800,
     */
    public record WhyCanNotPlayCardThere(@JsonValue int value) {}

    public enum CommandRejectionReasonType {
        CardRejected,
        GlobalCooldown,
        Cooldown,
        OutOfChargesCooldown,
        NotEnoughPower,
        SpellDoesNotExist,
        EntityDoesNotExist,
        InvalidEntityType,
        CanNotCast,
        EntityNotOwned,
        EntityOwnedBySomeoneElse,
        NoModeChange,
        EntityAlreadyInThisMode,
        ModeNotExist,
        InvalidCardIndex,
        InvalidCard;
    }
    /** Marker fo all CommandRejectionReason implementations */
    public interface CommandRejectionReason extends MultiType<CommandRejectionReasonType> {}
    /**  Rejection reason for `BuildHouse`, `ProduceSquad`, and `ProduceSquadOnBarrier` */
    public static final class CommandRejectionReasonCardRejected implements  CommandRejectionReason {
        @JsonProperty(required = true)
        private WhyCanNotPlayCardThere reason;
        @JsonProperty(required = true)
        private int[] failed_card_conditions;
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.CardRejected; }
        public WhyCanNotPlayCardThere getReason() { return reason; }
        public void setReason(WhyCanNotPlayCardThere v) { this.reason = v; }
        public int[] getFailedCardConditions() { return failed_card_conditions; }
        public void setFailedCardConditions(int[] v) { this.failed_card_conditions = v; }
        /**  Rejection reason for `BuildHouse`, `ProduceSquad`, and `ProduceSquadOnBarrier` */
        public CommandRejectionReasonCardRejected() { }
        /**  Rejection reason for `BuildHouse`, `ProduceSquad`, and `ProduceSquadOnBarrier` */
        public CommandRejectionReasonCardRejected(WhyCanNotPlayCardThere reason, int[] failed_card_conditions) {
            this.reason = reason;
            this.failed_card_conditions = failed_card_conditions;
        }
    }
    /**  You need to wait 10 ticks, after playing card, before playing another card */
    public static final class CommandRejectionReasonGlobalCooldown implements  CommandRejectionReason {
        @JsonProperty(required = true)
        private Tick cooldown_until;
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.GlobalCooldown; }
        public Tick getCooldownUntil() { return cooldown_until; }
        public void setCooldownUntil(Tick v) { this.cooldown_until = v; }
        /**  You need to wait 10 ticks, after playing card, before playing another card */
        public CommandRejectionReasonGlobalCooldown() { }
        /**  You need to wait 10 ticks, after playing card, before playing another card */
        public CommandRejectionReasonGlobalCooldown(Tick cooldown_until) {
            this.cooldown_until = cooldown_until;
        }
    }
    /**  Some spells do have a cooldown */
    public static final class CommandRejectionReasonCooldown implements  CommandRejectionReason {
        @JsonProperty(required = true)
        private Tick cooldown_until;
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.Cooldown; }
        public Tick getCooldownUntil() { return cooldown_until; }
        public void setCooldownUntil(Tick v) { this.cooldown_until = v; }
        /**  Some spells do have a cooldown */
        public CommandRejectionReasonCooldown() { }
        /**  Some spells do have a cooldown */
        public CommandRejectionReasonCooldown(Tick cooldown_until) {
            this.cooldown_until = cooldown_until;
        }
    }
    /**  You run out of charges you need to wait `(power cost) * 10 / 2` ticks, or spell cooldown, whatever is longer */
    public static final class CommandRejectionReasonOutOfChargesCooldown implements  CommandRejectionReason {
        @JsonProperty(required = true)
        private Tick cooldown_until;
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.OutOfChargesCooldown; }
        public Tick getCooldownUntil() { return cooldown_until; }
        public void setCooldownUntil(Tick v) { this.cooldown_until = v; }
        /**  You run out of charges you need to wait `(power cost) * 10 / 2` ticks, or spell cooldown, whatever is longer */
        public CommandRejectionReasonOutOfChargesCooldown() { }
        /**  You run out of charges you need to wait `(power cost) * 10 / 2` ticks, or spell cooldown, whatever is longer */
        public CommandRejectionReasonOutOfChargesCooldown(Tick cooldown_until) {
            this.cooldown_until = cooldown_until;
        }
    }
    /**  Player did not have enough power to play the card or activate the ability */
    public static final class CommandRejectionReasonNotEnoughPower implements  CommandRejectionReason {
        @JsonProperty(required = true)
        private float player_power;
        @JsonProperty(required = true)
        private short required;
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.NotEnoughPower; }
        public float getPlayerPower() { return player_power; }
        public void setPlayerPower(float v) { this.player_power = v; }
        public short getRequired() { return required; }
        public void setRequired(short v) { this.required = v; }
        /**  Player did not have enough power to play the card or activate the ability */
        public CommandRejectionReasonNotEnoughPower() { }
        /**  Player did not have enough power to play the card or activate the ability */
        public CommandRejectionReasonNotEnoughPower(float player_power, short required) {
            this.player_power = player_power;
            this.required = required;
        }
    }
    /**  Spell with given ID does not exist */
    public static final class CommandRejectionReasonSpellDoesNotExist implements  CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.SpellDoesNotExist; }
        /**  Spell with given ID does not exist */
        public CommandRejectionReasonSpellDoesNotExist() { }
    }
    /**  The entity is not on the map */
    public static final class CommandRejectionReasonEntityDoesNotExist implements  CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.EntityDoesNotExist; }
        /**  The entity is not on the map */
        public CommandRejectionReasonEntityDoesNotExist() { }
    }
    /**  Entity exist, but type is not correct */
    public static final class CommandRejectionReasonInvalidEntityType implements  CommandRejectionReason {
        @JsonProperty(required = true)
        private int entity_type;
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.InvalidEntityType; }
        public int getEntityType() { return entity_type; }
        public void setEntityType(int v) { this.entity_type = v; }
        /**  Entity exist, but type is not correct */
        public CommandRejectionReasonInvalidEntityType() { }
        /**  Entity exist, but type is not correct */
        public CommandRejectionReasonInvalidEntityType(int entity_type) {
            this.entity_type = entity_type;
        }
    }
    /**  Rejection reason for `CastSpellEntity` */
    public static final class CommandRejectionReasonCanNotCast implements  CommandRejectionReason {
        @JsonProperty(required = true)
        private int[] failed_spell_conditions;
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.CanNotCast; }
        public int[] getFailedSpellConditions() { return failed_spell_conditions; }
        public void setFailedSpellConditions(int[] v) { this.failed_spell_conditions = v; }
        /**  Rejection reason for `CastSpellEntity` */
        public CommandRejectionReasonCanNotCast() { }
        /**  Rejection reason for `CastSpellEntity` */
        public CommandRejectionReasonCanNotCast(int[] failed_spell_conditions) {
            this.failed_spell_conditions = failed_spell_conditions;
        }
    }
    /**  Bot issued command for entity that is not owned by anyone */
    public static final class CommandRejectionReasonEntityNotOwned implements  CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.EntityNotOwned; }
        /**  Bot issued command for entity that is not owned by anyone */
        public CommandRejectionReasonEntityNotOwned() { }
    }
    /**  Bot issued command for entity owned by someone else */
    public static final class CommandRejectionReasonEntityOwnedBySomeoneElse implements  CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.EntityOwnedBySomeoneElse; }
        /**  Bot issued command for entity owned by someone else */
        public CommandRejectionReasonEntityOwnedBySomeoneElse() { }
    }
    /**  Bot issued command for entity to change mode, but the entity does not have `ModeChange` aspect. */
    public static final class CommandRejectionReasonNoModeChange implements  CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.NoModeChange; }
        /**  Bot issued command for entity to change mode, but the entity does not have `ModeChange` aspect. */
        public CommandRejectionReasonNoModeChange() { }
    }
    /**  Trying to change to mode, in which the entity already is. */
    public static final class CommandRejectionReasonEntityAlreadyInThisMode implements  CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.EntityAlreadyInThisMode; }
        /**  Trying to change to mode, in which the entity already is. */
        public CommandRejectionReasonEntityAlreadyInThisMode() { }
    }
    /**  Trying to change to moe, that the entity does not have. */
    public static final class CommandRejectionReasonModeNotExist implements  CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.ModeNotExist; }
        /**  Trying to change to moe, that the entity does not have. */
        public CommandRejectionReasonModeNotExist() { }
    }
    /**  Card index must be 0-19 */
    public static final class CommandRejectionReasonInvalidCardIndex implements  CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.InvalidCardIndex; }
        /**  Card index must be 0-19 */
        public CommandRejectionReasonInvalidCardIndex() { }
    }
    /**  Card on the given index is invalid */
    public static final class CommandRejectionReasonInvalidCard implements  CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() { return CommandRejectionReasonType.InvalidCard; }
        /**  Card on the given index is invalid */
        public CommandRejectionReasonInvalidCard() { }
    }
    /**  Reason why command was rejected */
    public static class CommandRejectionReasonHolder {
        /**  Rejection reason for `BuildHouse`, `ProduceSquad`, and `ProduceSquadOnBarrier` */
        @JsonProperty(value = "CardRejected")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonCardRejected cardRejected;
        /**  You need to wait 10 ticks, after playing card, before playing another card */
        @JsonProperty(value = "GlobalCooldown")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonGlobalCooldown globalCooldown;
        /**  Some spells do have a cooldown */
        @JsonProperty(value = "Cooldown")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonCooldown cooldown;
        /**  You run out of charges you need to wait `(power cost) * 10 / 2` ticks, or spell cooldown, whatever is longer */
        @JsonProperty(value = "OutOfChargesCooldown")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonOutOfChargesCooldown outOfChargesCooldown;
        /**  Player did not have enough power to play the card or activate the ability */
        @JsonProperty(value = "NotEnoughPower")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonNotEnoughPower notEnoughPower;
        /**  Spell with given ID does not exist */
        @JsonProperty(value = "SpellDoesNotExist")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonSpellDoesNotExist spellDoesNotExist;
        /**  The entity is not on the map */
        @JsonProperty(value = "EntityDoesNotExist")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonEntityDoesNotExist entityDoesNotExist;
        /**  Entity exist, but type is not correct */
        @JsonProperty(value = "InvalidEntityType")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonInvalidEntityType invalidEntityType;
        /**  Rejection reason for `CastSpellEntity` */
        @JsonProperty(value = "CanNotCast")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonCanNotCast canNotCast;
        /**  Bot issued command for entity that is not owned by anyone */
        @JsonProperty(value = "EntityNotOwned")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonEntityNotOwned entityNotOwned;
        /**  Bot issued command for entity owned by someone else */
        @JsonProperty(value = "EntityOwnedBySomeoneElse")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonEntityOwnedBySomeoneElse entityOwnedBySomeoneElse;
        /**  Bot issued command for entity to change mode, but the entity does not have `ModeChange` aspect. */
        @JsonProperty(value = "NoModeChange")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonNoModeChange noModeChange;
        /**  Trying to change to mode, in which the entity already is. */
        @JsonProperty(value = "EntityAlreadyInThisMode")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonEntityAlreadyInThisMode entityAlreadyInThisMode;
        /**  Trying to change to moe, that the entity does not have. */
        @JsonProperty(value = "ModeNotExist")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonModeNotExist modeNotExist;
        /**  Card index must be 0-19 */
        @JsonProperty(value = "InvalidCardIndex")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonInvalidCardIndex invalidCardIndex;
        /**  Card on the given index is invalid */
        @JsonProperty(value = "InvalidCard")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonInvalidCard invalidCard;
        @JsonIgnore
        public CommandRejectionReason get() {
            if (cardRejected != null) {
                return cardRejected;
            }
            else if (globalCooldown != null) {
                return globalCooldown;
            }
            else if (cooldown != null) {
                return cooldown;
            }
            else if (outOfChargesCooldown != null) {
                return outOfChargesCooldown;
            }
            else if (notEnoughPower != null) {
                return notEnoughPower;
            }
            else if (spellDoesNotExist != null) {
                return spellDoesNotExist;
            }
            else if (entityDoesNotExist != null) {
                return entityDoesNotExist;
            }
            else if (invalidEntityType != null) {
                return invalidEntityType;
            }
            else if (canNotCast != null) {
                return canNotCast;
            }
            else if (entityNotOwned != null) {
                return entityNotOwned;
            }
            else if (entityOwnedBySomeoneElse != null) {
                return entityOwnedBySomeoneElse;
            }
            else if (noModeChange != null) {
                return noModeChange;
            }
            else if (entityAlreadyInThisMode != null) {
                return entityAlreadyInThisMode;
            }
            else if (modeNotExist != null) {
                return modeNotExist;
            }
            else if (invalidCardIndex != null) {
                return invalidCardIndex;
            }
            else if (invalidCard != null) {
                return invalidCard;
            }
            else {
                throw new IllegalStateException("CommandRejectionReasonHolder doesn't contain any CommandRejectionReason. Check implementation and API!");
            }
        }
        public CommandRejectionReasonHolder() { }
        public CommandRejectionReasonHolder(CommandRejectionReason v) {
            Objects.requireNonNull(v, "CommandRejectionReason must not be null");
            switch (v.getType()) {
                case CommandRejectionReasonType.CardRejected:
                    this.cardRejected = (CommandRejectionReasonCardRejected) v;
                    break;
                case CommandRejectionReasonType.GlobalCooldown:
                    this.globalCooldown = (CommandRejectionReasonGlobalCooldown) v;
                    break;
                case CommandRejectionReasonType.Cooldown:
                    this.cooldown = (CommandRejectionReasonCooldown) v;
                    break;
                case CommandRejectionReasonType.OutOfChargesCooldown:
                    this.outOfChargesCooldown = (CommandRejectionReasonOutOfChargesCooldown) v;
                    break;
                case CommandRejectionReasonType.NotEnoughPower:
                    this.notEnoughPower = (CommandRejectionReasonNotEnoughPower) v;
                    break;
                case CommandRejectionReasonType.SpellDoesNotExist:
                    this.spellDoesNotExist = (CommandRejectionReasonSpellDoesNotExist) v;
                    break;
                case CommandRejectionReasonType.EntityDoesNotExist:
                    this.entityDoesNotExist = (CommandRejectionReasonEntityDoesNotExist) v;
                    break;
                case CommandRejectionReasonType.InvalidEntityType:
                    this.invalidEntityType = (CommandRejectionReasonInvalidEntityType) v;
                    break;
                case CommandRejectionReasonType.CanNotCast:
                    this.canNotCast = (CommandRejectionReasonCanNotCast) v;
                    break;
                case CommandRejectionReasonType.EntityNotOwned:
                    this.entityNotOwned = (CommandRejectionReasonEntityNotOwned) v;
                    break;
                case CommandRejectionReasonType.EntityOwnedBySomeoneElse:
                    this.entityOwnedBySomeoneElse = (CommandRejectionReasonEntityOwnedBySomeoneElse) v;
                    break;
                case CommandRejectionReasonType.NoModeChange:
                    this.noModeChange = (CommandRejectionReasonNoModeChange) v;
                    break;
                case CommandRejectionReasonType.EntityAlreadyInThisMode:
                    this.entityAlreadyInThisMode = (CommandRejectionReasonEntityAlreadyInThisMode) v;
                    break;
                case CommandRejectionReasonType.ModeNotExist:
                    this.modeNotExist = (CommandRejectionReasonModeNotExist) v;
                    break;
                case CommandRejectionReasonType.InvalidCardIndex:
                    this.invalidCardIndex = (CommandRejectionReasonInvalidCardIndex) v;
                    break;
                case CommandRejectionReasonType.InvalidCard:
                    this.invalidCard = (CommandRejectionReasonInvalidCard) v;
                    break;
                default: throw new IllegalArgumentException("Unknown CommandRejectionReason " + v.getType());
            }
        }
        public CommandRejectionReasonCardRejected getCardRejected() { return cardRejected; }
        public CommandRejectionReasonGlobalCooldown getGlobalCooldown() { return globalCooldown; }
        public CommandRejectionReasonCooldown getCooldown() { return cooldown; }
        public CommandRejectionReasonOutOfChargesCooldown getOutOfChargesCooldown() { return outOfChargesCooldown; }
        public CommandRejectionReasonNotEnoughPower getNotEnoughPower() { return notEnoughPower; }
        public CommandRejectionReasonSpellDoesNotExist getSpellDoesNotExist() { return spellDoesNotExist; }
        public CommandRejectionReasonEntityDoesNotExist getEntityDoesNotExist() { return entityDoesNotExist; }
        public CommandRejectionReasonInvalidEntityType getInvalidEntityType() { return invalidEntityType; }
        public CommandRejectionReasonCanNotCast getCanNotCast() { return canNotCast; }
        public CommandRejectionReasonEntityNotOwned getEntityNotOwned() { return entityNotOwned; }
        public CommandRejectionReasonEntityOwnedBySomeoneElse getEntityOwnedBySomeoneElse() { return entityOwnedBySomeoneElse; }
        public CommandRejectionReasonNoModeChange getNoModeChange() { return noModeChange; }
        public CommandRejectionReasonEntityAlreadyInThisMode getEntityAlreadyInThisMode() { return entityAlreadyInThisMode; }
        public CommandRejectionReasonModeNotExist getModeNotExist() { return modeNotExist; }
        public CommandRejectionReasonInvalidCardIndex getInvalidCardIndex() { return invalidCardIndex; }
        public CommandRejectionReasonInvalidCard getInvalidCard() { return invalidCard; }
    }

    /**  Command that was rejected. */
    public static class RejectedCommand {
        @JsonProperty(required = true)
        private EntityId player;
        @JsonProperty(required = true)
        private CommandRejectionReasonHolder reason;
        @JsonProperty(required = true)
        private CommandHolder command;
        public EntityId getPlayer() { return player; }
        public void setPlayer(EntityId v) { this.player = v; }
        public CommandRejectionReasonHolder getReason() { return reason; }
        public void setReason(CommandRejectionReasonHolder v) { this.reason = v; }
        public CommandHolder getCommand() { return command; }
        public void setCommand(CommandHolder v) { this.command = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RejectedCommand that = (RejectedCommand) o;
            return getPlayer() == that.getPlayer() && getReason() == that.getReason() && getCommand() == that.getCommand();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getPlayer(), getReason(), getCommand());
        }
        @Override
        public String toString() {
            return "{" + "player: " + player + ", reason: " + reason + ", command: " + command + "}";
        }
        /**  Command that was rejected. */
        public RejectedCommand() { }
        /**  Command that was rejected. */
        public RejectedCommand(EntityId player, CommandRejectionReasonHolder reason, CommandHolder command) {
            this.player = player;
            this.reason = reason;
            this.command = command;
        }
    }

    /**  Response on the `/hello` endpoint. */
    public static class AiForMap {
        /**  The unique name of the bot. */
        @JsonProperty(required = true)
        private String name;
        /**  List of decks this bot can use on the map.
         *  Empty to signalize, that bot can not play on given map.
         */
        @JsonProperty(required = true)
        private Deck[] decks;
        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public Deck[] getDecks() { return decks; }
        public void setDecks(Deck[] v) { this.decks = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AiForMap that = (AiForMap) o;
            return getName() == that.getName() && getDecks() == that.getDecks();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getName(), getDecks());
        }
        @Override
        public String toString() {
            return "{" + "name: " + name + ", decks: " + decks + "}";
        }
        /**  Response on the `/hello` endpoint. */
        public AiForMap() { }
        /**  Response on the `/hello` endpoint. */
        public AiForMap(String name, Deck[] decks) {
            this.name = name;
            this.decks = decks;
        }
    }

    public static class MapEntities {
        @JsonProperty(required = true)
        private Projectile[] projectiles;
        @JsonProperty(required = true)
        private PowerSlot[] power_slots;
        @JsonProperty(required = true)
        private TokenSlot[] token_slots;
        @JsonProperty(required = true)
        private AbilityWorldObject[] ability_world_objects;
        @JsonProperty(required = true)
        private Squad[] squads;
        @JsonProperty(required = true)
        private Figure[] figures;
        @JsonProperty(required = true)
        private Building[] buildings;
        @JsonProperty(required = true)
        private BarrierSet[] barrier_sets;
        @JsonProperty(required = true)
        private BarrierModule[] barrier_modules;
        public Projectile[] getProjectiles() { return projectiles; }
        public void setProjectiles(Projectile[] v) { this.projectiles = v; }
        public PowerSlot[] getPowerSlots() { return power_slots; }
        public void setPowerSlots(PowerSlot[] v) { this.power_slots = v; }
        public TokenSlot[] getTokenSlots() { return token_slots; }
        public void setTokenSlots(TokenSlot[] v) { this.token_slots = v; }
        public AbilityWorldObject[] getAbilityWorldObjects() { return ability_world_objects; }
        public void setAbilityWorldObjects(AbilityWorldObject[] v) { this.ability_world_objects = v; }
        public Squad[] getSquads() { return squads; }
        public void setSquads(Squad[] v) { this.squads = v; }
        public Figure[] getFigures() { return figures; }
        public void setFigures(Figure[] v) { this.figures = v; }
        public Building[] getBuildings() { return buildings; }
        public void setBuildings(Building[] v) { this.buildings = v; }
        public BarrierSet[] getBarrierSets() { return barrier_sets; }
        public void setBarrierSets(BarrierSet[] v) { this.barrier_sets = v; }
        public BarrierModule[] getBarrierModules() { return barrier_modules; }
        public void setBarrierModules(BarrierModule[] v) { this.barrier_modules = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MapEntities that = (MapEntities) o;
            return getProjectiles() == that.getProjectiles() && getPowerSlots() == that.getPowerSlots() && getTokenSlots() == that.getTokenSlots() && getAbilityWorldObjects() == that.getAbilityWorldObjects() && getSquads() == that.getSquads() && getFigures() == that.getFigures() && getBuildings() == that.getBuildings() && getBarrierSets() == that.getBarrierSets() && getBarrierModules() == that.getBarrierModules();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getProjectiles(), getPowerSlots(), getTokenSlots(), getAbilityWorldObjects(), getSquads(), getFigures(), getBuildings(), getBarrierSets(), getBarrierModules());
        }
        @Override
        public String toString() {
            return "{" + "projectiles: " + projectiles + ", power_slots: " + power_slots + ", token_slots: " + token_slots + ", ability_world_objects: " + ability_world_objects + ", squads: " + squads + ", figures: " + figures + ", buildings: " + buildings + ", barrier_sets: " + barrier_sets + ", barrier_modules: " + barrier_modules + "}";
        }
        public MapEntities() { }
        public MapEntities(Projectile[] projectiles, PowerSlot[] power_slots, TokenSlot[] token_slots, AbilityWorldObject[] ability_world_objects, Squad[] squads, Figure[] figures, Building[] buildings, BarrierSet[] barrier_sets, BarrierModule[] barrier_modules) {
            this.projectiles = projectiles;
            this.power_slots = power_slots;
            this.token_slots = token_slots;
            this.ability_world_objects = ability_world_objects;
            this.squads = squads;
            this.figures = figures;
            this.buildings = buildings;
            this.barrier_sets = barrier_sets;
            this.barrier_modules = barrier_modules;
        }
    }

    /**  Used in `/start` endpoint. */
    public static class GameStartState {
        /**  Tells the bot which player it is supposed to control.
         *  If bot is only spectating, this is the ID of player that it is spectating for
         */
        @JsonProperty(required = true)
        private EntityId your_player_id;
        /**  Players in the match. */
        @JsonProperty(required = true)
        private MatchPlayer[] players;
        @JsonProperty(required = true)
        private MapEntities entities;
        public EntityId getYourPlayerId() { return your_player_id; }
        public void setYourPlayerId(EntityId v) { this.your_player_id = v; }
        public MatchPlayer[] getPlayers() { return players; }
        public void setPlayers(MatchPlayer[] v) { this.players = v; }
        public MapEntities getEntities() { return entities; }
        public void setEntities(MapEntities v) { this.entities = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GameStartState that = (GameStartState) o;
            return getYourPlayerId() == that.getYourPlayerId() && getPlayers() == that.getPlayers() && getEntities() == that.getEntities();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getYourPlayerId(), getPlayers(), getEntities());
        }
        @Override
        public String toString() {
            return "{" + "your_player_id: " + your_player_id + ", players: " + players + ", entities: " + entities + "}";
        }
        /**  Used in `/start` endpoint. */
        public GameStartState() { }
        /**  Used in `/start` endpoint. */
        public GameStartState(EntityId your_player_id, MatchPlayer[] players, MapEntities entities) {
            this.your_player_id = your_player_id;
            this.players = players;
            this.entities = entities;
        }
    }

    /**  Used in `/tick` endpoint, on every tick from 2 forward. */
    public static class GameState {
        /**  Tells the bot which player it is supposed to control.
         *  If bot is only spectating, this is the ID of player that it is spectating for
         */
        @JsonProperty(required = true)
        private EntityId your_player_id;
        /**  Time since start of the match measured in ticks.
         *  One tick is 0.1 second = 100 milliseconds = (10 ticks per second)
         *  Each tick is 100 ms. 1 second is 10 ticks. 1 minute is 600 ticks.
         */
        @JsonProperty(required = true)
        private Tick current_tick;
        /**  Commands that will be executed this tick. */
        @JsonProperty(required = true)
        private PlayerCommand[] commands;
        /**  Commands that was rejected. */
        @JsonProperty(required = true)
        private RejectedCommand[] rejected_commands;
        /**  player entities in the match */
        @JsonProperty(required = true)
        private PlayerEntity[] players;
        @JsonProperty(required = true)
        private MapEntities entities;
        public EntityId getYourPlayerId() { return your_player_id; }
        public void setYourPlayerId(EntityId v) { this.your_player_id = v; }
        public Tick getCurrentTick() { return current_tick; }
        public void setCurrentTick(Tick v) { this.current_tick = v; }
        public PlayerCommand[] getCommands() { return commands; }
        public void setCommands(PlayerCommand[] v) { this.commands = v; }
        public RejectedCommand[] getRejectedCommands() { return rejected_commands; }
        public void setRejectedCommands(RejectedCommand[] v) { this.rejected_commands = v; }
        public PlayerEntity[] getPlayers() { return players; }
        public void setPlayers(PlayerEntity[] v) { this.players = v; }
        public MapEntities getEntities() { return entities; }
        public void setEntities(MapEntities v) { this.entities = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GameState that = (GameState) o;
            return getYourPlayerId() == that.getYourPlayerId() && getCurrentTick() == that.getCurrentTick() && getCommands() == that.getCommands() && getRejectedCommands() == that.getRejectedCommands() && getPlayers() == that.getPlayers() && getEntities() == that.getEntities();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getYourPlayerId(), getCurrentTick(), getCommands(), getRejectedCommands(), getPlayers(), getEntities());
        }
        @Override
        public String toString() {
            return "{" + "your_player_id: " + your_player_id + ", current_tick: " + current_tick + ", commands: " + commands + ", rejected_commands: " + rejected_commands + ", players: " + players + ", entities: " + entities + "}";
        }
        /**  Used in `/tick` endpoint, on every tick from 2 forward. */
        public GameState() { }
        /**  Used in `/tick` endpoint, on every tick from 2 forward. */
        public GameState(EntityId your_player_id, Tick current_tick, PlayerCommand[] commands, RejectedCommand[] rejected_commands, PlayerEntity[] players, MapEntities entities) {
            this.your_player_id = your_player_id;
            this.current_tick = current_tick;
            this.commands = commands;
            this.rejected_commands = rejected_commands;
            this.players = players;
            this.entities = entities;
        }
    }

    /**  Used in `/prepare` endpoint */
    public static class Prepare {
        /**  Name of deck, selected from `AiForMap` returned by `/hello` endpoint. */
        @JsonProperty(required = true)
        private String deck;
        /**  Repeating `map_info` in case bot want to prepare differently based on map. */
        @JsonProperty(required = true)
        private MapInfo map_info;
        public String getDeck() { return deck; }
        public void setDeck(String v) { this.deck = v; }
        public MapInfo getMapInfo() { return map_info; }
        public void setMapInfo(MapInfo v) { this.map_info = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Prepare that = (Prepare) o;
            return getDeck() == that.getDeck() && getMapInfo() == that.getMapInfo();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getDeck(), getMapInfo());
        }
        @Override
        public String toString() {
            return "{" + "deck: " + deck + ", map_info: " + map_info + "}";
        }
        /**  Used in `/prepare` endpoint */
        public Prepare() { }
        /**  Used in `/prepare` endpoint */
        public Prepare(String deck, MapInfo map_info) {
            this.deck = deck;
            this.map_info = map_info;
        }
    }

    /**  Used in `/hello` endpoint */
    public static class ApiHello {
        /**  Must match the version in this file, to guarantee structures matching. */
        @JsonProperty(required = true)
        private long version;
        /**  Map about which is the game asking. */
        @JsonProperty(required = true)
        private MapInfo map;
        public long getVersion() { return version; }
        public void setVersion(long v) { this.version = v; }
        public MapInfo getMap() { return map; }
        public void setMap(MapInfo v) { this.map = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ApiHello that = (ApiHello) o;
            return getVersion() == that.getVersion() && getMap() == that.getMap();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getVersion(), getMap());
        }
        @Override
        public String toString() {
            return "{" + "version: " + version + ", map: " + map + "}";
        }
        /**  Used in `/hello` endpoint */
        public ApiHello() { }
        /**  Used in `/hello` endpoint */
        public ApiHello(long version, MapInfo map) {
            this.version = version;
            this.map = map;
        }
    }

}
