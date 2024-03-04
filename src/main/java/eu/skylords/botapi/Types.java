package eu.skylords.botapi;

import com.fasterxml.jackson.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

public class Types {
    public static class ApiVersion {
        public static final long VERSION = 16;
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
        U2(2000000),
        U3(3000000);

        //----------------------------------------
        public final int value;

        Upgrade(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        public static Optional<Upgrade> fromValue(int value) {
            return Arrays.stream(Upgrade.values())
                    .filter(u -> u.value == value)
                    .findFirst();
        }
    }

    /** ID of the squad resource */
    public record SquadId(@JsonValue int value) {}
    /** ID of the building resource */
    public record BuildingId(@JsonValue int value) {}
    /** ID of the spell resource */
    public record SpellId(@JsonValue int value) {}
    /** ID of the ability resource */
    public record AbilityId(@JsonValue int value) {}
    /** ID of the mode resource */
    public record ModeId(@JsonValue int value) {}
    /** ID of an entity present in the match unique to that match
     * First entity have ID 1, next 2, ...
     * Ids are never reused
     */
    public record EntityId(@JsonValue int value) {}
    /** Time information; 1 tick = 0.1s = 100 ms */
    public record Tick(@JsonValue int value) {}
    /** Difference between two `Ticks` (points in times, remaining time, ...) */
    public record TickCount(@JsonValue int value) {}

    /** Community map information */
    public static class CommunityMapInfo {
        /** Name of the map */
        @JsonProperty(required = true)
        private String name;
        /** Checksum of the map */
        @JsonProperty(required = true)
        private long crc;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public long getCrc() {
            return crc;
        }
        public void setCrc(long crc) {
            this.crc = crc;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CommunityMapInfo that = (CommunityMapInfo) o;
            return getCrc() == that.getCrc() && Objects.equals(getName(), that.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getCrc());
        }

        @Override
        public String toString() {
            return name + "[" + crc + "]";
        }
    }

    /**
     * Official spectator maps are normal maps (have unique id) so only `map` field is needed.
     * Community maps have additional information stored in the communityMapDetails.
     */
    public static class MapInfo {
        /** Represents the map, unfortunately EA decided, it will be harder for community maps. */
        @JsonProperty(required = true)
        private Maps map;
        /** Only relevant for community maps. */
        @JsonProperty(value = "community_map_details")
        private CommunityMapInfo communityMapDetails;

        public Maps getMap() {
            return map;
        }
        public void setMap(Maps map) {
            this.map = map;
        }

        public CommunityMapInfo getCommunityMapDetails() {
            return communityMapDetails;
        }
        public void setCommunityMapDetails(CommunityMapInfo communityMapDetails) {
            this.communityMapDetails = communityMapDetails;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MapInfo mapInfo = (MapInfo) o;
            return getMap() == mapInfo.getMap() && Objects.equals(getCommunityMapDetails(), mapInfo.getCommunityMapDetails());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getMap(), getCommunityMapDetails());
        }

        @Override
        public String toString() {
            return map + (Objects.nonNull(communityMapDetails) ? "_" + communityMapDetails : "");
        }
    }

    /** ID of the card resource */
    public static class Card {
        private final int cardId;
        private final CardTemplate cardTemplate;
        private final Upgrade upgrade;

        /** Creates a Card from the given {@link CardTemplate} with the provided {@link Upgrade Upgrade} */
        public Card(CardTemplate template, Upgrade upgrade) {
            Objects.requireNonNull(template, "Can't create a Card without template");
            Objects.requireNonNull(upgrade, "Can't create a Card without upgrade");
            this.cardTemplate = template;
            this.upgrade = CardTemplate.NotACard.equals(template) ? Upgrade.U0 : Upgrade.U3;
            this.cardId = template.getId() + upgrade.getValue();
        }

        /** Creates a Card from the given {@link CardTemplate} with the maximum {@link Upgrade Upgrade} */
        public Card(CardTemplate template) {
            this(template, Upgrade.U3);
        }

        /**
         * Creates a Card from the given card id.
         * The id contains the {@link CardTemplate} and the {@link Upgrade Upgrade}
         * @throws IllegalArgumentException if no CardTemplate exists for the provided id
         */
        @JsonCreator
        public Card(final int cardId) {
            int id = cardId;
            if (id > Upgrade.U3.getValue()) {
                this.upgrade = Upgrade.U3;
                id -= Upgrade.U3.getValue();
            } else if (id > Upgrade.U2.getValue()) {
                this.upgrade = Upgrade.U2;
                id -= Upgrade.U2.getValue();
            } else if (id > Upgrade.U1.getValue()) {
                this.upgrade = Upgrade.U1;
                id -= Upgrade.U1.getValue();
            } else {
                this.upgrade = Upgrade.U0;
            }
            this.cardTemplate = CardTemplate
                    .fromId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Can't determine Card with id " + cardId));
            this.cardId = this.cardTemplate.getId() + this.upgrade.getValue();
        }

        @JsonValue
        public int getCardId() {
            return cardId;
        }
        public CardTemplate getCardTemplate() {
            return cardTemplate;
        }
        public Upgrade getUpgrade() {
            return upgrade;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Card card = (Card) o;
            return getCardId() == card.getCardId() && getCardTemplate() == card.getCardTemplate() && getUpgrade() == card.getUpgrade();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getCardId(), getCardTemplate(), getUpgrade());
        }

        @Override
        public String toString() {
            return cardTemplate + "[" + upgrade + "]";
        }
    }

    public static class Deck {

        /**
         * Name of the deck, must be unique across decks used by bot, but different bots can have same deck names.
         * Must not contain spaces, to be addable in game.
         */
        @JsonProperty(required = true)
        private String name;
        /**  Index of the card that will be the deck icon; 0 to 19 */
        @JsonProperty(value = "cover_card_index", required = true)
        private int coverCardIndex;
        /** List of 20 cards in deck. Fill empty spaces with `NotACard`. */
        @JsonProperty(required = true)
        private List<Card> cards;

        public Deck() {}

        /**
         *
         * @param name Name of the deck. Must be unique across decks used by bot and must not contain any spaces.
         * @param cards List of up to 20 cards that are used in this deck. Must contain at least one card.
         * @param coverCardIndex Index of the card to use as index (0 - 19).
         *                       Make sure the index matches an actual card and not {@link CardTemplate#NotACard}
         */
        public Deck(String name, int coverCardIndex, List<Card> cards) {
            this.name = name;
            this.coverCardIndex = coverCardIndex;
            this.cards = cards;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name != null
                    ? name.trim().replace(" ", "_")
                    : null;
        }

        public int getCoverCardIndex() {
            return coverCardIndex;
        }
        public void setCoverCardIndex(int coverCardIndex) {
            this.coverCardIndex = coverCardIndex;
        }

        public List<Card> getCards() {
            return cards;
        }

        /**
         * Set the {@link Card Cards} of this card deck.
         * Max 20 cards per deck, each card must be present only once.
         * Automatically limits the deck to 20 cards and removes duplicates.
         * @param cards List of Cards to add to the deck.
         * @throws IllegalArgumentException if no card is provided
         */
        public void setCards(List<Card> cards) {
            this.cards = new ArrayList<>();

            // check if deck contains at least one card
            if (cards == null || cards.isEmpty()) {
                throw new IllegalArgumentException("Deck must contain at least one card");
            }
            // deck must not use more than 20 cards
            else if (cards.size() > 20) {
                this.cards.addAll(cards.subList(0, 20));
                System.out.println("A deck can only contain up to 20 cards -> only the first 20 cards are used");
            } else {
                this.cards.addAll(cards);
            }

            Set<CardTemplate> seenBefore = new HashSet<>();
            this.cards = cards.stream()
                    .map(x -> {
                        if (!CardTemplate.NotACard.equals(x.cardTemplate) && !seenBefore.add(x.cardTemplate)) {
                            System.out.println("Removed duplicate card from deck. A specific card can only be used once in a deck!");
                            return new Card(CardTemplate.NotACard);
                        } else {
                            return x;
                        }
                    })
                    .collect(Collectors.toList());

            while (this.cards.size() < 20) {
                cards.add(new Card(CardTemplate.NotACard));
            }
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
        OnMorph(294);

        // ----------------------------------------------------------------------
        public final int id;

        AbilityLine(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Optional<AbilityLine> fromId(int id) {
            return Arrays.stream(AbilityLine.values())
                    .filter(al -> al.id == id)
                    .findFirst();
        }
    }

    public enum AbilityEffectType {
        DamageRadialArea,
        DamageOverTime,
        LinkedFire,
        Other;
    }

    /** Marker fo all AbilityEffect implementations */
    public interface AbilityEffectSpecific extends MultiType<AbilityEffectType> {
    }

    public static final class AbilityEffectSpecificDamageRadialArea implements AbilityEffectSpecific {
        @JsonProperty(value = "progress_current", required = true)
        private float progressCurrent;
        @JsonProperty(value = "progress_delta", required = true)
        private float progressDelta;
        @JsonProperty(value = "damage_remaining", required = true)
        private float damageRemaining;

        @Override
        @JsonIgnore
        public AbilityEffectType getType() {
            return AbilityEffectType.DamageRadialArea;
        }

        public float getProgressCurrent() {
            return progressCurrent;
        }
        public void setProgressCurrent(float progressCurrent) {
            this.progressCurrent = progressCurrent;
        }

        public float getProgressDelta() {
            return progressDelta;
        }
        public void setProgressDelta(float progressDelta) {
            this.progressDelta = progressDelta;
        }

        public float getDamageRemaining() {
            return damageRemaining;
        }
        public void setDamageRemaining(float damageRemaining) {
            this.damageRemaining = damageRemaining;
        }
    }

    public static final class AbilityEffectSpecificDamageOverTime implements AbilityEffectSpecific {
        @JsonProperty(value = "tick_wait_duration", required = true)
        private TickCount tickWaitDuration;
        @JsonProperty(value = "ticks_left", required = true)
        private TickCount ticksLeft;
        @JsonProperty(value = "tick_damage", required = true)
        private float tickDamage;

        @Override
        public AbilityEffectType getType() {
            return AbilityEffectType.DamageOverTime;
        }

        public TickCount getTickWaitDuration() {
            return tickWaitDuration;
        }
        public void setTickWaitDuration(TickCount tickWaitDuration) {
            this.tickWaitDuration = tickWaitDuration;
        }

        public TickCount getTicksLeft() {
            return ticksLeft;
        }
        public void setTicksLeft(TickCount ticksLeft) {
            this.ticksLeft = ticksLeft;
        }

        public float getTickDamage() {
            return tickDamage;
        }
        public void setTickDamage(float tickDamage) {
            this.tickDamage = tickDamage;
        }
    }

    public static final class AbilityEffectSpecificLinkedFire implements AbilityEffectSpecific {
        @JsonProperty(value = "linked", required = true)
        private boolean linked;
        @JsonProperty(value = "fighting", required = true)
        private boolean fighting;
        @JsonProperty(value = "fast_cast", required = true)
        private int fastCast;
        @JsonProperty(value = "support_cap", required = true)
        private short supportCap;
        @JsonProperty(value = "support_production", required = true)
        private byte supportProduction;

        @Override
        @JsonIgnore
        public AbilityEffectType getType() {
            return AbilityEffectType.LinkedFire;
        }

        public boolean isLinked() {
            return linked;
        }
        public void setLinked(boolean linked) {
            this.linked = linked;
        }

        public boolean isFighting() {
            return fighting;
        }
        public void setFighting(boolean fighting) {
            this.fighting = fighting;
        }

        public int getFastCast() {
            return fastCast;
        }
        public void setFastCast(int fastCast) {
            this.fastCast = fastCast;
        }

        public short getSupportCap() {
            return supportCap;
        }
        public void setSupportCap(short supportCap) {
            this.supportCap = supportCap;
        }

        public byte getSupportProduction() {
            return supportProduction;
        }
        public void setSupportProduction(byte supportProduction) {
            this.supportProduction = supportProduction;
        }
    }

    /** If you think something interesting got hidden by Other report it */
    public static final class AbilityEffectSpecificOther implements AbilityEffectSpecific {
        @Override
        @JsonIgnore
        public AbilityEffectType getType() {
            return AbilityEffectType.Other;
        }
    }

    /**
     * Wrapper for AbilityEffectSpecific implementations.
     * Helps to determine the AbilityEffect after json deserialization.
     */
    public static class AbilityEffectSpecificHolder {
        @JsonProperty(value = "DamageRadialArea")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificDamageRadialArea damageRadialArea;

        @JsonProperty(value = "DamageOverTime")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificDamageOverTime damageOverTime;

        @JsonProperty(value = "LinkedFire")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificLinkedFire linkedFire;

        /** If you think something interesting got hidden by Other report it */
        @JsonProperty(value = "Other")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AbilityEffectSpecificOther other;

        @JsonIgnore
        public AbilityEffectSpecific get() {
            if (damageRadialArea != null) {
                return damageRadialArea;
            }
            else if (damageOverTime != null) {
                return damageOverTime;
            }
            else if (linkedFire != null) {
                return linkedFire;
            }
            else if (other != null) {
                return other;
            } else {
                throw new IllegalStateException("AbilityEffectSpecificHolder doesn't contain any AbilityEffectSpecific. Check implementation and API!");
            }
        }
        public AbilityEffectSpecificHolder() { }
        public AbilityEffectSpecificHolder(AbilityEffectSpecific abilityEffectSpecific) {
            Objects.requireNonNull(abilityEffectSpecific, "AbilityEffect must not be null");
            switch (abilityEffectSpecific.getType()) {
                case AbilityEffectType.DamageRadialArea:
                    this.damageRadialArea = (AbilityEffectSpecificDamageRadialArea) abilityEffectSpecific;
                    break;
                case AbilityEffectType.DamageOverTime:
                    damageOverTime = (AbilityEffectSpecificDamageOverTime) abilityEffectSpecific;
                    break;
                case AbilityEffectType.LinkedFire:
                    linkedFire = (AbilityEffectSpecificLinkedFire) abilityEffectSpecific;
                    break;
                case AbilityEffectType.Other:
                    other = (AbilityEffectSpecificOther) abilityEffectSpecific;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown AbilityEffectSpecific " + abilityEffectSpecific.getType());
            }
        }

        public AbilityEffectSpecificDamageRadialArea getDamageRadialArea() {
            return damageRadialArea;
        }
        public void setDamageRadialArea(AbilityEffectSpecificDamageRadialArea damageRadialArea) {
            this.damageRadialArea = damageRadialArea;
        }

        public AbilityEffectSpecificDamageOverTime getDamageOverTime() {
            return damageOverTime;
        }
        public void setDamageOverTime(AbilityEffectSpecificDamageOverTime damageOverTime) {
            this.damageOverTime = damageOverTime;
        }

        public AbilityEffectSpecificLinkedFire getLinkedFire() {
            return linkedFire;
        }
        public void setLinkedFire(AbilityEffectSpecificLinkedFire linkedFire) {
            this.linkedFire = linkedFire;
        }

        public AbilityEffectSpecificOther getOther() {
            return other;
        }
        public void setOther(AbilityEffectSpecificOther other) {
            this.other = other;
        }
    }


    public static class AbilityEffect {
        @JsonProperty(required = true)
        private AbilityId id;
        @JsonProperty(required = true)
        private AbilityLine line;
        @JsonProperty(required = true)
        private EntityId source;
        @JsonProperty(value = "source_team", required = true)
        private byte sourceTeam;
        @JsonProperty(value = "start_tick")
        private Tick startTick;
        @JsonProperty(value = "end_tick")
        private Tick endTick;
        @JsonProperty(required = true)
        private AbilityEffectSpecificHolder specific;

        public AbilityId getId() {
            return id;
        }
        public void setId(AbilityId id) {
            this.id = id;
        }

        public AbilityLine getLine() {
            return line;
        }
        public void setLine(AbilityLine line) {
            this.line = line;
        }

        public EntityId getSource() {
            return source;
        }
        public void setSource(EntityId source) {
            this.source = source;
        }

        public byte getSourceTeam() {
            return sourceTeam;
        }
        public void setSourceTeam(byte sourceTeam) {
            this.sourceTeam = sourceTeam;
        }

        public Tick getStartTick() {
            return startTick;
        }
        public void setStartTick(Tick startTick) {
            this.startTick = startTick;
        }

        public Tick getEndTick() {
            return endTick;
        }
        public void setEndTick(Tick endTick) {
            this.endTick = endTick;
        }

        public AbilityEffectSpecificHolder getSpecific() {
            return specific;
        }
        public void setSpecific(AbilityEffectSpecificHolder specific) {
            this.specific = specific;
        }
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

    public interface Aspect extends MultiType<AspectType> {
    }

    /** Used by *mostly* power wells */
    public static final class AspectPowerProduction implements Aspect {
        @JsonProperty(value = "current_power", required = true)
        private float currentPower;
        @JsonProperty(value = "power_capacity", required = true)
        private float powerCapacity;

        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.PowerProduction;
        }

        public float getCurrentPower() {
            return currentPower;
        }
        public void setCurrentPower(float currentPower) {
            this.currentPower = currentPower;
        }

        public float getPowerCapacity() {
            return powerCapacity;
        }
        public void setPowerCapacity(float powerCapacity) {
            this.powerCapacity = powerCapacity;
        }
    }

    /**  Health of an entity.*/
    public static final class AspectHealth implements Aspect {
        @JsonProperty(value = "current_hp", required = true)
        private float currentHp;
        @JsonProperty(value = "cap_current_max", required = true)
        private float capCurrentMax;

        @Override
        @JsonIgnore
        public AspectType getType() {
            return null;
        }

        public float getCurrentHp() {
            return currentHp;
        }
        public void setCurrentHp(float currentHp) {
            this.currentHp = currentHp;
        }

        public float getCapCurrentMax() {
            return capCurrentMax;
        }
        public void setCapCurrentMax(float capCurrentMax) {
            this.capCurrentMax = capCurrentMax;
        }
    }

    public static final class AspectCombat implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.Combat;
        }
    }

    public static final class AspectModeChange implements Aspect {
        @JsonProperty(value = "current_mode", required = true)
        public ModeId currentMode;
        @JsonProperty(value = "all_modes", required = true)
        public List<ModeId> allModes;

        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.ModeChange;
        }

        public ModeId getCurrentMode() {
            return currentMode;
        }
        public void setCurrentMode(ModeId currentMode) {
            this.currentMode = currentMode;
        }

        public List<ModeId> getAllModes() {
            return allModes;
        }
        public void setAllModes(List<ModeId> allModes) {
            this.allModes = allModes;
        }
    }

    public static final class AspectAmmunition implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.Ammunition;
        }
    }

    public static final class AspectSuperWeaponShadow implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.SuperWeaponShadow;
        }
    }

    public static final class AspectWormMovement implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.WormMovement;
        }
    }

    public static final class AspectNPCTag implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.NPCTag;
        }
    }

    public static final class AspectPlayerKit implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.PlayerKit;
        }
    }

    public static final class AspectLoot implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.Loot;
        }
    }

    public static final class AspectImmunity implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.Immunity;
        }
    }

    public static final class AspectTurret implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.Turret;
        }
    }

    public static final class AspectTunnel implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.Tunnel;
        }
    }

    public static final class AspectMountBarrier implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.MountBarrier;
        }
    }

    public static final class AspectSpellMemory implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.SpellMemory;
        }
    }

    public static final class AspectPortal implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.Portal;
        }
    }

    public static final class AspectHate implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.Hate;
        }
    }

    public static final class AspectBarrierGate implements Aspect {
        @JsonProperty(required = true)
        public boolean open;

        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.BarrierGate;
        }

        public boolean isOpen() {
            return open;
        }
        public void setOpen(boolean open) {
            this.open = open;
        }
    }

    public static final class AspectAttackable implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.Attackable;
        }
    }

    public static final class AspectSquadRefill implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.SquadRefill;
        }
    }

    public static final class AspectPortalExit implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.PortalExit;
        }
    }

    /** When building / barrier is under construction it has this aspect. */
    public static final class AspectConstructionData implements Aspect {
        @JsonProperty(value = "refresh_count_remaining", required = true)
        private TickCount refreshCountRemaining;
        @JsonProperty(value = "refresh_count_total", required = true)
        private TickCount refreshCountTotal;
        @JsonProperty(value = "health_per_build_update_trigger", required = true)
        private float healthPerBuildUpdateTrigger;
        @JsonProperty(value = "remaining_health_to_add", required = true)
        private float remainingHealthToAdd;

        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.ConstructionData;
        }

        public TickCount getRefreshCountRemaining() {
            return refreshCountRemaining;
        }
        public void setRefreshCountRemaining(TickCount refreshCountRemaining) {
            this.refreshCountRemaining = refreshCountRemaining;
        }

        public TickCount getRefreshCountTotal() {
            return refreshCountTotal;
        }
        public void setRefreshCountTotal(TickCount refreshCountTotal) {
            this.refreshCountTotal = refreshCountTotal;
        }

        public float getHealthPerBuildUpdateTrigger() {
            return healthPerBuildUpdateTrigger;
        }
        public void setHealthPerBuildUpdateTrigger(float healthPerBuildUpdateTrigger) {
            this.healthPerBuildUpdateTrigger = healthPerBuildUpdateTrigger;
        }

        public float getRemainingHealthToAdd() {
            return remainingHealthToAdd;
        }
        public void setRemainingHealthToAdd(float remainingHealthToAdd) {
            this.remainingHealthToAdd = remainingHealthToAdd;
        }
    }

    public static final class AspectSuperWeaponShadowBomb implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.SuperWeaponShadowBomb;
        }
    }

    public static final class AspectRepairBarrierSet implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.RepairBarrierSet;
        }
    }

    public static final class AspectConstructionRepair implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.ConstructionRepair;
        }
    }

    public static final class AspectFollower implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.Follower;
        }
    }

    public static final class AspectCollisionBase implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.CollisionBase;
        }
    }

    public static final class AspectEditorUniqueID implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.EditorUniqueID;
        }
    }

    public static final class AspectRoam implements Aspect {
        @Override
        @JsonIgnore
        public AspectType getType() {
            return AspectType.Roam;
        }
    }

    /**
     * Wrapper for Aspect implementations.
     * Helps to determine the type of Aspect after json deserialization.
     */
    public static class AspectHolder {
        /** Used by *mostly* power wells */
        @JsonProperty(value = "PowerProduction")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectPowerProduction powerProduction;
        /** Health of an entity. */
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
        private AspectNPCTag nPCTag;
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
        /** When building / barrier is under construction it has this aspect. */
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
        private AspectEditorUniqueID editorUniqueID;
        @JsonProperty(value = "Roam")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private AspectRoam roam;

        @JsonIgnore
        public Aspect get() {
            if (powerProduction != null) { return powerProduction; }
            else if (health != null) { return health; }
            else if (combat != null) { return combat; }
            else if (modeChange != null) { return modeChange; }
            else if (ammunition != null) { return ammunition; }
            else if (superWeaponShadow != null) { return superWeaponShadow; }
            else if (wormMovement != null) { return wormMovement; }
            else if (nPCTag != null) { return nPCTag; }
            else if (playerKit != null) { return playerKit; }
            else if (loot != null) { return loot; }
            else if (immunity != null) { return immunity; }
            else if (turret != null) { return turret; }
            else if (tunnel != null) { return tunnel; }
            else if (mountBarrier != null) { return mountBarrier; }
            else if (spellMemory != null) { return spellMemory; }
            else if (portal != null) { return portal; }
            else if (hate != null) { return hate; }
            else if (barrierGate != null) { return barrierGate; }
            else if (attackable != null) { return attackable; }
            else if (squadRefill != null) { return squadRefill; }
            else if (portalExit != null) { return portalExit; }
            else if (constructionData != null) { return constructionData; }
            else if (superWeaponShadowBomb != null) { return superWeaponShadowBomb; }
            else if (repairBarrierSet != null) { return repairBarrierSet; }
            else if (constructionRepair != null) { return constructionRepair; }
            else if (follower != null) { return follower; }
            else if (collisionBase != null) { return collisionBase; }
            else if (editorUniqueID != null) { return editorUniqueID; }
            else if (roam != null) { return roam; }
            else { throw new IllegalStateException("AspectHolder doesn't contain any AbilityEffectSpecific. Check implementation and API!"); }
        }

        public AspectHolder() { }
        public AspectHolder(Aspect aspect) {
            Objects.requireNonNull(aspect, "Aspect must not be null");
            switch (aspect.getType()) {
                case AspectType.PowerProduction:
                    powerProduction = (AspectPowerProduction) aspect;
                    break;
                case AspectType.Health:
                    health = (AspectHealth) aspect;
                    break;
                case AspectType.Combat:
                    combat = (AspectCombat) aspect;
                    break;
                case AspectType.ModeChange:
                    modeChange = (AspectModeChange) aspect;
                    break;
                case AspectType.Ammunition:
                    ammunition = (AspectAmmunition) aspect;
                    break;
                case AspectType.SuperWeaponShadow:
                    superWeaponShadow = (AspectSuperWeaponShadow) aspect;
                    break;
                case AspectType.WormMovement:
                    wormMovement = (AspectWormMovement) aspect;
                    break;
                case AspectType.NPCTag:
                    nPCTag = (AspectNPCTag) aspect;
                    break;
                case AspectType.PlayerKit:
                    playerKit = (AspectPlayerKit) aspect;
                    break;
                case AspectType.Loot:
                    loot = (AspectLoot) aspect;
                    break;
                case AspectType.Immunity:
                    immunity = (AspectImmunity) aspect;
                    break;
                case AspectType.Turret:
                    turret = (AspectTurret) aspect;
                    break;
                case AspectType.Tunnel:
                    tunnel = (AspectTunnel) aspect;
                    break;
                case AspectType.MountBarrier:
                    mountBarrier = (AspectMountBarrier) aspect;
                    break;
                case AspectType.SpellMemory:
                    spellMemory = (AspectSpellMemory) aspect;
                    break;
                case AspectType.Portal:
                    portal = (AspectPortal) aspect;
                    break;
                case AspectType.Hate:
                    hate = (AspectHate) aspect;
                    break;
                case AspectType.BarrierGate:
                    barrierGate = (AspectBarrierGate) aspect;
                    break;
                case AspectType.Attackable:
                    attackable = (AspectAttackable) aspect;
                    break;
                case AspectType.SquadRefill:
                    squadRefill = (AspectSquadRefill) aspect;
                    break;
                case AspectType.PortalExit:
                    portalExit = (AspectPortalExit) aspect;
                    break;
                case AspectType.ConstructionData:
                    constructionData = (AspectConstructionData) aspect;
                    break;
                case AspectType.SuperWeaponShadowBomb:
                    superWeaponShadowBomb = (AspectSuperWeaponShadowBomb) aspect;
                    break;
                case AspectType.RepairBarrierSet:
                    repairBarrierSet = (AspectRepairBarrierSet) aspect;
                    break;
                case AspectType.ConstructionRepair:
                    constructionRepair = (AspectConstructionRepair) aspect;
                    break;
                case AspectType.Follower:
                    follower = (AspectFollower) aspect;
                    break;
                case AspectType.CollisionBase:
                    collisionBase = (AspectCollisionBase) aspect;
                    break;
                case AspectType.EditorUniqueID:
                    editorUniqueID = (AspectEditorUniqueID) aspect;
                    break;
                case AspectType.Roam:
                    roam = (AspectRoam) aspect;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown AbilityEffectSpecific " + aspect.getType());
            }
        }

        public AspectPowerProduction getPowerProduction() {
            return powerProduction;
        }
        public void setPowerProduction(AspectPowerProduction powerProduction) {
            this.powerProduction = powerProduction;
        }

        public AspectHealth getHealth() {
            return health;
        }
        public void setHealth(AspectHealth health) {
            this.health = health;
        }

        public AspectCombat getCombat() {
            return combat;
        }
        public void setCombat(AspectCombat combat) {
            this.combat = combat;
        }

        public AspectModeChange getModeChange() {
            return modeChange;
        }
        public void setModeChange(AspectModeChange modeChange) {
            this.modeChange = modeChange;
        }

        public AspectAmmunition getAmmunition() {
            return ammunition;
        }
        public void setAmmunition(AspectAmmunition ammunition) {
            this.ammunition = ammunition;
        }

        public AspectSuperWeaponShadow getSuperWeaponShadow() {
            return superWeaponShadow;
        }
        public void setSuperWeaponShadow(AspectSuperWeaponShadow superWeaponShadow) {
            this.superWeaponShadow = superWeaponShadow;
        }

        public AspectWormMovement getWormMovement() {
            return wormMovement;
        }
        public void setWormMovement(AspectWormMovement wormMovement) {
            this.wormMovement = wormMovement;
        }

        public AspectNPCTag getnPCTag() {
            return nPCTag;
        }
        public void setnPCTag(AspectNPCTag nPCTag) {
            this.nPCTag = nPCTag;
        }

        public AspectPlayerKit getPlayerKit() {
            return playerKit;
        }
        public void setPlayerKit(AspectPlayerKit playerKit) {
            this.playerKit = playerKit;
        }

        public AspectLoot getLoot() {
            return loot;
        }
        public void setLoot(AspectLoot loot) {
            this.loot = loot;
        }

        public AspectImmunity getImmunity() {
            return immunity;
        }
        public void setImmunity(AspectImmunity immunity) {
            this.immunity = immunity;
        }

        public AspectTurret getTurret() {
            return turret;
        }
        public void setTurret(AspectTurret turret) {
            this.turret = turret;
        }

        public AspectTunnel getTunnel() {
            return tunnel;
        }
        public void setTunnel(AspectTunnel tunnel) {
            this.tunnel = tunnel;
        }

        public AspectMountBarrier getMountBarrier() {
            return mountBarrier;
        }
        public void setMountBarrier(AspectMountBarrier mountBarrier) {
            this.mountBarrier = mountBarrier;
        }

        public AspectSpellMemory getSpellMemory() {
            return spellMemory;
        }
        public void setSpellMemory(AspectSpellMemory spellMemory) {
            this.spellMemory = spellMemory;
        }

        public AspectPortal getPortal() {
            return portal;
        }
        public void setPortal(AspectPortal portal) {
            this.portal = portal;
        }

        public AspectHate getHate() {
            return hate;
        }
        public void setHate(AspectHate hate) {
            this.hate = hate;
        }

        public AspectBarrierGate getBarrierGate() {
            return barrierGate;
        }
        public void setBarrierGate(AspectBarrierGate barrierGate) {
            this.barrierGate = barrierGate;
        }

        public AspectAttackable getAttackable() {
            return attackable;
        }
        public void setAttackable(AspectAttackable attackable) {
            this.attackable = attackable;
        }

        public AspectSquadRefill getSquadRefill() {
            return squadRefill;
        }
        public void setSquadRefill(AspectSquadRefill squadRefill) {
            this.squadRefill = squadRefill;
        }

        public AspectPortalExit getPortalExit() {
            return portalExit;
        }
        public void setPortalExit(AspectPortalExit portalExit) {
            this.portalExit = portalExit;
        }

        public AspectConstructionData getConstructionData() {
            return constructionData;
        }
        public void setConstructionData(AspectConstructionData constructionData) {
            this.constructionData = constructionData;
        }

        public AspectSuperWeaponShadowBomb getSuperWeaponShadowBomb() {
            return superWeaponShadowBomb;
        }
        public void setSuperWeaponShadowBomb(AspectSuperWeaponShadowBomb superWeaponShadowBomb) {
            this.superWeaponShadowBomb = superWeaponShadowBomb;
        }

        public AspectRepairBarrierSet getRepairBarrierSet() {
            return repairBarrierSet;
        }
        public void setRepairBarrierSet(AspectRepairBarrierSet repairBarrierSet) {
            this.repairBarrierSet = repairBarrierSet;
        }

        public AspectConstructionRepair getConstructionRepair() {
            return constructionRepair;
        }
        public void setConstructionRepair(AspectConstructionRepair constructionRepair) {
            this.constructionRepair = constructionRepair;
        }

        public AspectFollower getFollower() {
            return follower;
        }
        public void setFollower(AspectFollower follower) {
            this.follower = follower;
        }

        public AspectCollisionBase getCollisionBase() {
            return collisionBase;
        }
        public void setCollisionBase(AspectCollisionBase collisionBase) {
            this.collisionBase = collisionBase;
        }

        public AspectEditorUniqueID getEditorUniqueID() {
            return editorUniqueID;
        }
        public void setEditorUniqueID(AspectEditorUniqueID editorUniqueID) {
            this.editorUniqueID = editorUniqueID;
        }

        public AspectRoam getRoam() {
            return roam;
        }
        public void setRoam(AspectRoam roam) {
            this.roam = roam;
        }
    }

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
        OrbColor(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        public static Optional<OrbColor> fromValue(int value) {
            return Arrays.stream(OrbColor.values())
                    .filter(oc -> oc.value == value)
                    .findFirst();
        }
    }

    /** Subset of `OrbColor`, because creating the other colors does not make sense. */
    public enum CreateOrbColor {
        Shadow(1),
        Nature(2),
        Frost(3),
        Fire(4);

        //----------------------------------------
        public final int value;
        CreateOrbColor(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        public static Optional<CreateOrbColor> fromValue(int value) {
            return Arrays.stream(CreateOrbColor.values())
                    .filter(coc -> coc.value == value)
                    .findFirst();
        }
    }

    /** Simplified version of how many monuments of each color player have */
    public static class Orbs {
        @JsonProperty(required = true)
        private byte shadow;
        @JsonProperty(required = true)
        private byte nature;
        @JsonProperty(required = true)
        private byte frost;
        @JsonProperty(required = true)
        private byte fire;
        /** Can be used instead of any color, and then changes to color of first token on the used card. */
        @JsonProperty(required = true)
        private byte starting;
        /** Can be used only for colorless tokens on the card. (Curse Orb changes colored orb to white one) */
        @JsonProperty(required = true)
        private byte white;
        /** Can be used as any color. Only provided by map scripts (and Amii Monument Card?) */
        @JsonProperty(required = true)
        private byte all;

        public byte getShadow() {
            return shadow;
        }
        public void setShadow(byte shadow) {
            this.shadow = shadow;
        }

        public byte getNature() {
            return nature;
        }
        public void setNature(byte nature) {
            this.nature = nature;
        }

        public byte getFrost() {
            return frost;
        }
        public void setFrost(byte frost) {
            this.frost = frost;
        }

        public byte getFire() {
            return fire;
        }
        public void setFire(byte fire) {
            this.fire = fire;
        }

        public byte getStarting() {
            return starting;
        }
        public void setStarting(byte starting) {
            this.starting = starting;
        }

        public byte getWhite() {
            return white;
        }
        public void setWhite(byte white) {
            this.white = white;
        }

        public byte getAll() {
            return all;
        }
        public void setAll(byte all) {
            this.all = all;
        }
    }

    /**
     * Technically it is specific case of `Entity`, but we decided to move players out,
     * and move few fields up like position and owning player id.
     */
    public static class PlayerEntity {
        /** Unique id of the entity */
        @JsonProperty(value = "id", required = true)
        private EntityId id;
        /** List of effects the entity have. */
        @JsonProperty(value = "effects", required = true)
        private List<AbilityEffect> effects;
        /** List of aspects entity have. */
        @JsonProperty(required = true)
        private List<AspectHolder> aspects;
        @JsonProperty(required = true)
        private byte team;
        @JsonProperty(required = true)
        private float power;
        @JsonProperty(value = "void_power", required = true)
        private float voidPower;
        @JsonProperty(value = "population_count", required = true)
        private int populationCount;
        @JsonProperty(required = true)
        private String name;
        @JsonProperty(required = true)
        private Orbs orbs;

        public EntityId getId() {
            return id;
        }
        public void setId(EntityId id) {
            this.id = id;
        }

        public List<AbilityEffect> getEffects() {
            return effects;
        }
        public void setEffects(List<AbilityEffect> effects) {
            this.effects = effects;
        }

        public List<AspectHolder> getAspects() {
            return aspects;
        }
        public void setAspects(List<AspectHolder> aspects) {
            this.aspects = aspects;
        }

        public byte getTeam() {
            return team;
        }
        public void setTeam(byte team) {
            this.team = team;
        }

        public float getPower() {
            return power;
        }
        public void setPower(float power) {
            this.power = power;
        }

        public float getVoidPower() {
            return voidPower;
        }
        public void setVoidPower(float voidPower) {
            this.voidPower = voidPower;
        }

        public int getPopulationCount() {
            return populationCount;
        }
        public void setPopulationCount(int populationCount) {
            this.populationCount = populationCount;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public Orbs getOrbs() {
            return orbs;
        }
        public void setOrbs(Orbs orbs) {
            this.orbs = orbs;
        }
    }

    public static class MatchPlayer {
        /** Name of player. */
        @JsonProperty(required = true)
        private String name;
        /** TODO Due to technical difficulties might be empty. */
        @JsonProperty(required = true)
        private Deck deck;
        /** entity controlled by this player */
        @JsonProperty(required = true)
        private PlayerEntity entity;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public Deck getDeck() {
            return deck;
        }
        public void setDeck(Deck deck) {
            this.deck = deck;
        }

        public PlayerEntity getEntity() {
            return entity;
        }
        public void setEntity(PlayerEntity entity) {
            this.entity = entity;
        }
    }

    /**
     * `x` and `z` are coordinates on the 2D map.
     * Be aware when using or converting to {@link Position2D},
     * because it uses x and y to represent the 2d coordinates.
     */
    public static class Position {
        /** width */
        @JsonProperty(required = true)
        private float x;
        /** Also known as height. */
        @JsonProperty(required = true)
        private float y;
        /** depth */
        @JsonProperty(required = true)
        private float z;

        /** Converts this Position to Position2D */
        @JsonIgnore
        public Position2D toPosition2d() {
            return new Position2D(x, z);
        }

        public float getX() {
            return x;
        }
        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }
        public void setY(float y) {
            this.y = y;
        }

        public float getZ() {
            return z;
        }
        public void setZ(float z) {
            this.z = z;
        }
    }

    public static class Position2D {
        @JsonIgnore
        private static final Position2D Zero = new Position2D(0, 0);

        @JsonProperty(required = true)
        private float x;
        @JsonProperty(required = true)
        private float y;

        public Position2D() {}
        public Position2D(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }
        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }
        public void setY(float y) {
            this.y = y;
        }
    }

    public static class Position2DWithOrientation extends Position2D {
        /**
         * in default camera orientation
         * 0 = down, π/2 = right, π = up, π3/2 = left
         */
        @JsonProperty(required = true)
        private float orientation;

        public float getOrientation() {
            return orientation;
        }

        public void setOrientation(float orientation) {
            this.orientation = orientation;
        }
    }

    public enum TargetType {
        Single,
        Multi;
    }

    public enum SingleTargetType {
        Entity,
        Location;
    }

    public interface Target extends MultiType<TargetType> {
    }

    public interface SingleTarget extends MultiType<SingleTargetType> {
    }

    /** Target entity */
    public static final class SingleTargetSingleEntity implements SingleTarget {

        @JsonProperty(required = true)
        private EntityId id;

        @Override
        @JsonIgnore
        public SingleTargetType getType() {
            return SingleTargetType.Entity;
        }

        public EntityId getId() {
            return id;
        }
        public void setId(EntityId id) {
            this.id = id;
        }
    }

    /** Target location on the ground */
    public static final class SingleTargetLocation implements SingleTarget {
        @JsonProperty(required = true)
        private Position2D xy;

        @Override
        @JsonIgnore
        public SingleTargetType getType() {
            return SingleTargetType.Location;
        }

        public Position2D getXy() {
            return xy;
        }
        public void setXy(Position2D xy) {
            this.xy = xy;
        }
    }

    /** When targeting you can target either entity, or ground coordinates. */
    public static class SingleTargetHolder {
        /** Target entity */
        @JsonProperty(value = "SingleEntity")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private SingleTargetSingleEntity singleEntity;
        /** Target location on the ground */
        @JsonProperty("Location")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private SingleTargetLocation location;

        @JsonIgnore
        public SingleTarget get() {
            if (singleEntity != null) { return singleEntity; }
            if (location != null) { return location; }
            else { throw new IllegalStateException("SingleTargetHolder doesn't contain any SingleTarget. Check implementation and API!"); }
        }
        public SingleTargetHolder() { }
        public SingleTargetHolder(SingleTarget target) {
            Objects.requireNonNull(target, "SingleTarget must not be null");
            switch (target.getType()) {
                case SingleTargetType.Entity:
                    singleEntity = (SingleTargetSingleEntity) target;
                    break;
                case SingleTargetType.Location:
                    location = (SingleTargetLocation) target;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown SingleTarget " + target.getType());
            }
        }

        public SingleTargetSingleEntity getSingleEntity() {
            return singleEntity;
        }
        public void setSingleEntity(SingleTargetSingleEntity singleEntity) {
            this.singleEntity = singleEntity;
        }

        public SingleTargetLocation getLocation() {
            return location;
        }
        public void setLocation(SingleTargetLocation location) {
            this.location = location;
        }
    }


    public static final class TargetSingle implements Target {

        @JsonProperty(required = true)
        private SingleTargetHolder single;

        @Override
        public TargetType getType() {
            return TargetType.Single;
        }

        public SingleTargetHolder getSingle() {
            return single;
        }
        public void setSingle(SingleTargetHolder single) {
            this.single = single;
        }
    }

    public static final class TargetMulti implements Target {

        @JsonProperty(value = "xy_begin", required = true)
        private Position2D xyBegin;
        @JsonProperty(value = "xy_end", required = true)
        private Position2D xyEnd;

        @Override
        public TargetType getType() {
            return TargetType.Multi;
        }

        public Position2D getXyBegin() {
            return xyBegin;
        }
        public void setXyBegin(Position2D xyBegin) {
            this.xyBegin = xyBegin;
        }

        public Position2D getXyEnd() {
            return xyEnd;
        }
        public void setXyEnd(Position2D xyEnd) {
            this.xyEnd = xyEnd;
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
            if (single != null) { return single; }
            if (multi != null) { return multi; }
            else { throw new IllegalStateException("TargetHolder doesn't contain any Target. Check implementation and API!"); }
        }
        public TargetHolder() { }
        public TargetHolder(Target target) {
            Objects.requireNonNull(target, "Target must not be null");
            switch (target.getType()) {
                case TargetType.Single:
                    single = (TargetSingle) target;
                    break;
                case TargetType.Multi:
                    multi = (TargetMulti) target;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Target " + target.getType());
            }
        }

        public TargetSingle getSingle() {
            return single;
        }
        public void setSingle(TargetSingle single) {
            this.single = single;
        }

        public TargetMulti getMulti() {
            return multi;
        }
        public void setMulti(TargetMulti multi) {
            this.multi = multi;
        }
    }

    public enum WalkMode {
        PartialForce(1),
        Force(2),
        /** Also called by players "Attack move", or "Q move" */
        Normal(4),
        Crusade(5),
        Scout(6),
        Patrol(7);

        //----------------------------------------
        public final int value;
        WalkMode(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        public static Optional<WalkMode> fromValue(int value) {
            return Arrays.stream(WalkMode.values())
                    .filter(wm -> wm.value == value)
                    .findFirst();
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

    public interface Job extends MultiType<JobType>{
    }

    public static final class JobNoJob implements Job {
        @JsonIgnore
        public JobType getType() {
            return JobType.NoJob;
        }
    }

    public static final class JobIdle implements Job {
        @JsonIgnore
        public JobType getType() {
            return JobType.Idle;
        }
    }

    public static final class JobGoto implements Job {
        @JsonProperty(value = "waypoints", required = true)
        private List<Position2DWithOrientation> waypoints;
        @JsonProperty(value = "target_entity_id")
        private EntityId targetEntityId;
        @JsonProperty(value = "walk_mode", required = true)
        private WalkMode walkMode;

        @JsonIgnore
        public JobType getType() {
            return JobType.Goto;
        }

        public List<Position2DWithOrientation> getWaypoints() {
            return waypoints;
        }
        public void setWaypoints(List<Position2DWithOrientation> waypoints) {
            this.waypoints = waypoints;
        }

        public EntityId getTargetEntityId() {
            return targetEntityId;
        }
        public void setTargetEntityId(EntityId targetEntityId) {
            this.targetEntityId = targetEntityId;
        }

        public WalkMode getWalkMode() {
            return walkMode;
        }
        public void setWalkMode(WalkMode walkMode) {
            this.walkMode = walkMode;
        }
    }

    public static final class JobAttackMelee implements Job {
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(value = "use_force_goto", required = true)
        public boolean useForceGoto;
        @JsonProperty(value = "no_move", required = true)
        private boolean noMove;
        @JsonProperty(value = "too_close_range", required = true)
        private float tooCloseRange;

        @JsonIgnore
        public JobType getType() {
            return JobType.AttackMelee;
        }

        public TargetHolder getTarget() {
            return target;
        }
        public void setTarget(TargetHolder target) {
            this.target = target;
        }

        public boolean isUseForceGoto() {
            return useForceGoto;
        }
        public void setUseForceGoto(boolean useForceGoto) {
            this.useForceGoto = useForceGoto;
        }

        public boolean isNoMove() {
            return noMove;
        }
        public void setNoMove(boolean noMove) {
            this.noMove = noMove;
        }

        public float getTooCloseRange() {
            return tooCloseRange;
        }
        public void setTooCloseRange(float tooCloseRange) {
            this.tooCloseRange = tooCloseRange;
        }
    }

    public static final class JobCastSpell implements Job {
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(value = "spell_id", required = true)
        private SpellId spellId;
        @JsonProperty(value = "use_force_goto", required = true)
        private boolean useForceGoto;
        @JsonProperty(value = "no_move", required = true)
        private boolean noMove;

        @JsonIgnore
        public JobType getType() {
            return JobType.CastSpell;
        }

        public Types.TargetHolder getTarget() {
            return target;
        }
        public void setTarget(Types.TargetHolder target) {
            this.target = target;
        }

        public Types.SpellId getSpellId() {
            return spellId;
        }
        public void setSpellId(Types.SpellId spellId) {
            this.spellId = spellId;
        }

        public boolean isUseForceGoto() {
            return useForceGoto;
        }
        public void setUseForceGoto(boolean useForceGoto) {
            this.useForceGoto = useForceGoto;
        }

        public boolean isNoMove() {
            return noMove;
        }
        public void setNoMove(boolean noMove) {
            this.noMove = noMove;
        }
    }

    public static final class JobDie implements Job {
        @JsonIgnore
        public JobType getType() {
            return JobType.Die;
        }
    }

    public static final class JobTalk implements Job {
        @JsonProperty(required = true)
        private EntityId target;
        @JsonProperty(value = "walk_to_target", required = true)
        private boolean walkToTarget;

        @JsonIgnore
        public JobType getType() {
            return JobType.Talk;
        }

        public EntityId getTarget() {
            return target;
        }
        public void setTarget(EntityId target) {
            this.target = target;
        }

        public boolean isWalkToTarget() {
            return walkToTarget;
        }
        public void setWalkToTarget(boolean walkToTarget) {
            this.walkToTarget = walkToTarget;
        }
    }

    public static final class JobScriptTalk implements Job {
        @JsonProperty(value = "hide_weapon", required = true)
        private boolean hideWeapon;

        @JsonIgnore
        public JobType getType() {
            return JobType.ScriptTalk;
        }

        public boolean isHideWeapon() {
            return hideWeapon;
        }
        public void setHideWeapon(boolean hideWeapon) {
            this.hideWeapon = hideWeapon;
        }
    }

    public static final class JobFreeze implements Job {
        @JsonProperty(value = "end_step", required = true)
        private Tick endStep;
        @JsonProperty(required = true)
        private EntityId source;
        @JsonProperty(value = "spell_id", required = true)
        private SpellId spellId;
        @JsonProperty(required = true)
        private TickCount duration;
        @JsonProperty(value = "delay_ability", required = true)
        private TickCount delayAbility;
        @JsonProperty(value = "ability_id_while_frozen", required = true)
        private List<AbilityId> abilityIdWhileFrozen;
        @JsonProperty(value = "ability_id_delayed", required = true)
        private List<AbilityId> abilityIdDelayed;
        @JsonProperty(value = "ability_line_id_cancel_on_start", required = true)
        private AbilityLine abilityLineIdCancelOnStart;
        @JsonProperty(value = "pushback_immunity", required = true)
        private boolean pushbackImmunity;
        @JsonProperty(required = true)
        private int mode;

        @JsonIgnore
        public JobType getType() {
            return JobType.Freeze;
        }

        public Tick getEndStep() {
            return endStep;
        }
        public void setEndStep(Tick endStep) {
            this.endStep = endStep;
        }

        public EntityId getSource() {
            return source;
        }
        public void setSource(EntityId source) {
            this.source = source;
        }

        public Types.SpellId getSpellId() {
            return spellId;
        }
        public void setSpellId(Types.SpellId spellId) {
            this.spellId = spellId;
        }

        public Types.TickCount getDuration() {
            return duration;
        }
        public void setDuration(Types.TickCount duration) {
            this.duration = duration;
        }

        public Types.TickCount getDelayAbility() {
            return delayAbility;
        }
        public void setDelayAbility(Types.TickCount delayAbility) {
            this.delayAbility = delayAbility;
        }

        public List<AbilityId> getAbilityIdWhileFrozen() {
            return abilityIdWhileFrozen;
        }
        public void setAbilityIdWhileFrozen(List<AbilityId> abilityIdWhileFrozen) {
            this.abilityIdWhileFrozen = abilityIdWhileFrozen;
        }

        public List<AbilityId> getAbilityIdDelayed() {
            return abilityIdDelayed;
        }
        public void setAbilityIdDelayed(List<AbilityId> abilityIdDelayed) {
            this.abilityIdDelayed = abilityIdDelayed;
        }

        public AbilityLine getAbilityLineIdCancelOnStart() {
            return abilityLineIdCancelOnStart;
        }
        public void setAbilityLineIdCancelOnStart(AbilityLine abilityLineIdCancelOnStart) {
            this.abilityLineIdCancelOnStart = abilityLineIdCancelOnStart;
        }

        public boolean isPushbackImmunity() {
            return pushbackImmunity;
        }
        public void setPushbackImmunity(boolean pushbackImmunity) {
            this.pushbackImmunity = pushbackImmunity;
        }

        public int getMode() {
            return mode;
        }
        public void setMode(int mode) {
            this.mode = mode;
        }
    }

    public static final class JobSpawn implements Job {
        @JsonProperty(required = true)
        private TickCount duration;
        @JsonProperty(value = "end_step", required = true)
        private Tick endStep;

        @JsonIgnore
        public JobType getType() {
            return JobType.Spawn;
        }

        public Types.TickCount getDuration() {
            return duration;
        }
        public void setDuration(Types.TickCount duration) {
            this.duration = duration;
        }

        public Tick getEndStep() {
            return endStep;
        }
        public void setEndStep(Tick endStep) {
            this.endStep = endStep;
        }
    }

    public static final class JobCheer implements Job {
        @JsonIgnore
        public JobType getType() {
            return JobType.Cheer;
        }
    }

    public static final class JobAttackSquad implements Job {
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(value = "weapon_type", required = true)
        private byte weaponType;
        @JsonProperty(required = true)
        private float damage;
        @JsonProperty(value = "range_min", required = true)
        private float rangeMin;
        @JsonProperty(value = "range_max", required = true)
        private float rangeMax;
        @JsonProperty(value = "attack_spell")
        public SpellId attackSpell;
        @JsonProperty(value = "use_force_goto", required = true)
        private boolean useForceGoto;
        @JsonProperty(value = "operation_range", required = true)
        private float operationRange;
        @JsonProperty(value = "no_move", required = true)
        private boolean noMove;
        @JsonProperty(value = "was_in_attack", required = true)
        private boolean wasInAttack;
        @JsonProperty(value = "melee_attack", required = true)
        private boolean meleeAttack;

        @JsonIgnore
        public JobType getType() {
            return JobType.AttackSquad;
        }

        public Types.TargetHolder getTarget() {
            return target;
        }
        public void setTarget(Types.TargetHolder target) {
            this.target = target;
        }

        public byte getWeaponType() {
            return weaponType;
        }
        public void setWeaponType(byte weaponType) {
            this.weaponType = weaponType;
        }

        public float getDamage() {
            return damage;
        }
        public void setDamage(float damage) {
            this.damage = damage;
        }

        public float getRangeMin() {
            return rangeMin;
        }
        public void setRangeMin(float rangeMin) {
            this.rangeMin = rangeMin;
        }

        public float getRangeMax() {
            return rangeMax;
        }
        public void setRangeMax(float rangeMax) {
            this.rangeMax = rangeMax;
        }

        public Types.SpellId getAttackSpell() {
            return attackSpell;
        }
        public void setAttackSpell(Types.SpellId attackSpell) {
            this.attackSpell = attackSpell;
        }

        public boolean isUseForceGoto() {
            return useForceGoto;
        }
        public void setUseForceGoto(boolean useForceGoto) {
            this.useForceGoto = useForceGoto;
        }

        public float getOperationRange() {
            return operationRange;
        }
        public void setOperationRange(float operationRange) {
            this.operationRange = operationRange;
        }

        public boolean isNoMove() {
            return noMove;
        }
        public void setNoMove(boolean noMove) {
            this.noMove = noMove;
        }

        public boolean isWasInAttack() {
            return wasInAttack;
        }
        public void setWasInAttack(boolean wasInAttack) {
            this.wasInAttack = wasInAttack;
        }

        public boolean isMeleeAttack() {
            return meleeAttack;
        }
        public void setMeleeAttack(boolean meleeAttack) {
            this.meleeAttack = meleeAttack;
        }
    }

    public static final class JobCastSpellSquad implements Job {
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(value = "spell_id", required = true)
        private SpellId spellId;
        @JsonProperty(value = "use_force_goto", required = true)
        private boolean useForceGoto;
        @JsonProperty(value = "spell_fired", required = true)
        private boolean spellFired;
        @JsonProperty(value = "spell_per_source_entity", required = true)
        private boolean spellPerSourceEntity;
        @JsonProperty(value = "was_in_attack", required = true)
        private boolean wasInAttack;

        @JsonIgnore
        public JobType getType() {
            return JobType.CastSpellSquad;
        }

        public Types.TargetHolder getTarget() {
            return target;
        }
        public void setTarget(Types.TargetHolder target) {
            this.target = target;
        }

        public Types.SpellId getSpellId() {
            return spellId;
        }
        public void setSpellId(Types.SpellId spellId) {
            this.spellId = spellId;
        }

        public boolean isUseForceGoto() {
            return useForceGoto;
        }
        public void setUseForceGoto(boolean useForceGoto) {
            this.useForceGoto = useForceGoto;
        }

        public boolean isSpellFired() {
            return spellFired;
        }
        public void setSpellFired(boolean spellFired) {
            this.spellFired = spellFired;
        }

        public boolean isSpellPerSourceEntity() {
            return spellPerSourceEntity;
        }
        public void setSpellPerSourceEntity(boolean spellPerSourceEntity) {
            this.spellPerSourceEntity = spellPerSourceEntity;
        }

        public boolean isWasInAttack() {
            return wasInAttack;
        }
        public void setWasInAttack(boolean wasInAttack) {
            this.wasInAttack = wasInAttack;
        }
    }

    public static final class JobPushBack implements Job {
        @JsonProperty(value = "start_coord", required = true)
        private Position2D startCoord;
        @JsonProperty(value = "target_coord", required = true)
        private Position2D targetCoord;
        @JsonProperty(required = true)
        private float speed;
        @JsonProperty(value = "rotation_speed", required = true)
        private float rotationSpeed;
        @JsonProperty(required = true)
        private float damage;
        @JsonProperty
        private EntityId source;

        @JsonIgnore
        public JobType getType() {
            return JobType.PushBack;
        }

        public Position2D getStartCoord() {
            return startCoord;
        }
        public void setStartCoord(Position2D startCoord) {
            this.startCoord = startCoord;
        }

        public Position2D getTargetCoord() {
            return targetCoord;
        }
        public void setTargetCoord(Position2D targetCoord) {
            this.targetCoord = targetCoord;
        }

        public float getSpeed() {
            return speed;
        }
        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public float getRotationSpeed() {
            return rotationSpeed;
        }
        public void setRotationSpeed(float rotationSpeed) {
            this.rotationSpeed = rotationSpeed;
        }

        public float getDamage() {
            return damage;
        }
        public void setDamage(float damage) {
            this.damage = damage;
        }

        public EntityId getSource() {
            return source;
        }
        public void setSource(EntityId source) {
            this.source = source;
        }
    }

    public static final class JobStampede implements Job {
        @JsonProperty(required = true)
        private SpellId spell;
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(value = "start_coord", required = true)
        private Position2D startCoord;

        @JsonIgnore
        public JobType getType() {
            return JobType.Stampede;
        }

        public Types.SpellId getSpell() {
            return spell;
        }
        public void setSpell(Types.SpellId spell) {
            this.spell = spell;
        }

        public Types.TargetHolder getTarget() {
            return target;
        }
        public void setTarget(Types.TargetHolder target) {
            this.target = target;
        }

        public Position2D getStartCoord() {
            return startCoord;
        }
        public void setStartCoord(Position2D startCoord) {
            this.startCoord = startCoord;
        }
    }

    public static final class JobBarrierCrush implements Job {
        @JsonIgnore
        public JobType getType() {
            return JobType.BarrierCrush;
        }
    }

    public static final class JobBarrierGateToggle implements Job {
        @JsonIgnore
        public JobType getType() {
            return JobType.BarrierGateToggle;
        }
    }

    public static final class JobFlameThrower implements Job {
        @JsonProperty(required = true)
        private TargetHolder target;
        @JsonProperty(value = "spell_id", required = true)
        private SpellId spellId;
        @JsonProperty(value = "duration_step_init", required = true)
        private TickCount durationStepInit;
        @JsonProperty(value = "duration_step_shut_down", required = true)
        private TickCount durationStepShutDown;

        @JsonIgnore
        public JobType getType() {
            return JobType.FlameThrower;
        }

        public SpellId getSpellId() {
            return spellId;
        }
        public void setSpellId(SpellId spellId) {
            this.spellId = spellId;
        }

        public TickCount getDurationStepInit() {
            return durationStepInit;
        }
        public void setDurationStepInit(TickCount durationStepInit) {
            this.durationStepInit = durationStepInit;
        }

        public TickCount getDurationStepShutDown() {
            return durationStepShutDown;
        }
        public void setDurationStepShutDown(TickCount durationStepShutDown) {
            this.durationStepShutDown = durationStepShutDown;
        }
}

    public static final class JobConstruct implements Job {
        @JsonProperty(value = "construction_update_steps", required = true)
        private TickCount ConstructionUpdateSteps;
        @JsonProperty(value = "construction_update_count_remaining", required = true)
        private TickCount ConstructionUpdateCountRemaining;

        @JsonIgnore
        public JobType getType() {
            return JobType.Construct;
        }

        public TickCount getConstructionUpdateSteps() {
            return ConstructionUpdateSteps;
        }
        public void setConstructionUpdateSteps(TickCount constructionUpdateSteps) {
            ConstructionUpdateSteps = constructionUpdateSteps;
        }

        public TickCount getConstructionUpdateCountRemaining() {
            return ConstructionUpdateCountRemaining;
        }
        public void setConstructionUpdateCountRemaining(TickCount constructionUpdateCountRemaining) {
            ConstructionUpdateCountRemaining = constructionUpdateCountRemaining;
        }
    }

    public static final class JobCrush implements Job {
        @JsonProperty(value = "crush_steps", required = true)
        private TickCount CrushSteps;
        @JsonProperty(value = "entity_update_steps", required = true)
        private TickCount EntityUpdateSteps;
        @JsonProperty(value = "remaining_crush_steps", required = true)
        private TickCount RemainingCrushSteps;

        @JsonIgnore
        public JobType getType() {
            return JobType.Crush;
        }

        public TickCount getCrushSteps() {
            return CrushSteps;
        }
        public void setCrushSteps(TickCount crushSteps) {
            CrushSteps = crushSteps;
        }

        public TickCount getEntityUpdateSteps() {
            return EntityUpdateSteps;
        }
        public void setEntityUpdateSteps(TickCount entityUpdateSteps) {
            EntityUpdateSteps = entityUpdateSteps;
        }

        public TickCount getRemainingCrushSteps() {
            return RemainingCrushSteps;
        }
        public void setRemainingCrushSteps(TickCount remainingCrushSteps) {
            RemainingCrushSteps = remainingCrushSteps;
        }
    }

    public static final class JobMountBarrierSquad implements Job {
        @JsonProperty(value = "barrier_module", required = true)
        private EntityId barrierModule;

        @JsonIgnore
        public JobType getType() {
            return JobType.MountBarrierSquad;
        }

        public EntityId getBarrierModule() {
            return barrierModule;
        }
        public void setBarrierModule(EntityId barrierModule) {
            this.barrierModule = barrierModule;
        }
    }

    public static final class JobMountBarrier implements Job {
        @JsonProperty(value = "current_barrier_module")
        private EntityId currentBarrierModule;
        @JsonProperty(value = "goal_barrier_module")
        private EntityId goalBarrierModule;

        @JsonIgnore
        public JobType getType() {
            return JobType.MountBarrier;
        }

        public EntityId getCurrentBarrierModule() {
            return currentBarrierModule;
        }
        public void setCurrentBarrierModule(EntityId currentBarrierModule) {
            this.currentBarrierModule = currentBarrierModule;
        }

        public EntityId getGoalBarrierModule() {
            return goalBarrierModule;
        }
        public void setGoalBarrierModule(EntityId goalBarrierModule) {
            this.goalBarrierModule = goalBarrierModule;
        }
    }

    public static final class JobModeChangeSquad implements Job {
        @JsonProperty(value = "new_mode", required = true)
        private ModeId newMode;
        @JsonProperty(value = "mode_change_done", required = true)
        private boolean modeChangeDone;

        @JsonIgnore
        public JobType getType() {
            return JobType.ModeChangeSquad;
        }

        public ModeId getNewMode() {
            return newMode;
        }
        public void setNewMode(ModeId newMode) {
            this.newMode = newMode;
        }

        public boolean isModeChangeDone() {
            return modeChangeDone;
        }
        public void setModeChangeDone(boolean modeChangeDone) {
            this.modeChangeDone = modeChangeDone;
        }
    }

    public static final class JobModeChange implements Job {
        @JsonProperty(value = "new_mode", required = true)
        private ModeId newMode;

        @JsonIgnore
        public JobType getType() {
            return JobType.ModeChange;
        }

        public ModeId getNewMode() {
            return newMode;
        }
        public void setNewMode(ModeId newMode) {
            this.newMode = newMode;
        }
    }

    public static final class JobSacrificeSquad implements Job {
        @JsonProperty(value = "target_entity", required = true)
        private EntityId targetEntity;

        @JsonIgnore
        public JobType getType() {
            return JobType.SacrificeSquad;
        }

        public EntityId getTargetEntity() {
            return targetEntity;
        }
        public void setTargetEntity(EntityId targetEntity) {
            this.targetEntity = targetEntity;
        }
    }

    public static final class JobUsePortalSquad implements Job {
        @JsonProperty(value = "target_entity_id", required = true)
        private EntityId targetEntityId;

        @JsonIgnore
        public JobType getType() {
            return JobType.UsePortalSquad;
        }

        public EntityId getTargetEntityId() {
            return targetEntityId;
        }
        public void setTargetEntityId(EntityId targetEntityId) {
            this.targetEntityId = targetEntityId;
        }
    }

    public static final class JobChannel implements Job {
        @JsonProperty(value = "target_squad_id")
        private EntityId targetSquadId;
        @JsonProperty(value = "mode_target_world", required = true)
        private boolean modeTargetWorld;
        @JsonProperty(value = "entity_id")
        private EntityId entityId;
        @JsonProperty(value = "spell_id", required = true)
        private SpellId spellId;
        @JsonProperty(value = "spell_id_on_target_on_finish")
        private SpellId spellIdOnTargetOnFinish;
        @JsonProperty(value = "spell_id_on_target_on_start")
        private SpellId spellIdOnTargetOnStart;
        @JsonProperty(value = "step_duration_until_finish", required = true)
        private TickCount stepDurationUntilFinish;
        @JsonProperty(value = "timing_channel_start", required = true)
        private int timingChannelStart;
        @JsonProperty(value = "timing_channel_loop", required = true)
        private int timingChannelLoop;
        @JsonProperty(value = "timing_channel_end", required = true)
        private int timingChannelEnd;
        @JsonProperty(value = "abort_on_out_of_range_squared", required = true)
        private float abortOnOutOfRangeSquared;
        @JsonProperty(value = "abort_check_failed", required = true)
        private boolean abortCheckFailed;
        @JsonProperty(value = "orientate_to_target", required = true)
        private boolean orientateToTarget;
        @JsonProperty(value = "orientate_to_target_max_step", required = true)
        private TickCount orientateToTargetMaxStep;
        @JsonProperty(value = "abort_on_owner_get_damaged", required = true)
        private boolean abortOnOwnerGetDamaged;
        @JsonProperty(value = "abort_on_mode_change", required = true)
        private boolean abortOnModeChange;

        @JsonIgnore
        public JobType getType() {
            return JobType.Channel;
        }

        public EntityId getTargetSquadId() {
            return targetSquadId;
        }
        public void setTargetSquadId(EntityId targetSquadId) {
            this.targetSquadId = targetSquadId;
        }

        public boolean isModeTargetWorld() {
            return modeTargetWorld;
        }
        public void setModeTargetWorld(boolean modeTargetWorld) {
            this.modeTargetWorld = modeTargetWorld;
        }

        public EntityId getEntityId() {
            return entityId;
        }
        public void setEntityId(EntityId entityId) {
            this.entityId = entityId;
        }

        public SpellId getSpellId() {
            return spellId;
        }
        public void setSpellId(SpellId spellId) {
            this.spellId = spellId;
        }

        public SpellId getSpellIdOnTargetOnFinish() {
            return spellIdOnTargetOnFinish;
        }
        public void setSpellIdOnTargetOnFinish(SpellId spellIdOnTargetOnFinish) {
            this.spellIdOnTargetOnFinish = spellIdOnTargetOnFinish;
        }

        public SpellId getSpellIdOnTargetOnStart() {
            return spellIdOnTargetOnStart;
        }
        public void setSpellIdOnTargetOnStart(SpellId spellIdOnTargetOnStart) {
            this.spellIdOnTargetOnStart = spellIdOnTargetOnStart;
        }

        public TickCount getStepDurationUntilFinish() {
            return stepDurationUntilFinish;
        }
        public void setStepDurationUntilFinish(TickCount stepDurationUntilFinish) {
            this.stepDurationUntilFinish = stepDurationUntilFinish;
        }

        public int getTimingChannelStart() {
            return timingChannelStart;
        }
        public void setTimingChannelStart(int timingChannelStart) {
            this.timingChannelStart = timingChannelStart;
        }

        public int getTimingChannelLoop() {
            return timingChannelLoop;
        }
        public void setTimingChannelLoop(int timingChannelLoop) {
            this.timingChannelLoop = timingChannelLoop;
        }

        public int getTimingChannelEnd() {
            return timingChannelEnd;
        }
        public void setTimingChannelEnd(int timingChannelEnd) {
            this.timingChannelEnd = timingChannelEnd;
        }

        public float getAbortOnOutOfRangeSquared() {
            return abortOnOutOfRangeSquared;
        }
        public void setAbortOnOutOfRangeSquared(float abortOnOutOfRangeSquared) {
            this.abortOnOutOfRangeSquared = abortOnOutOfRangeSquared;
        }

        public boolean isAbortCheckFailed() {
            return abortCheckFailed;
        }
        public void setAbortCheckFailed(boolean abortCheckFailed) {
            this.abortCheckFailed = abortCheckFailed;
        }

        public boolean isOrientateToTarget() {
            return orientateToTarget;
        }
        public void setOrientateToTarget(boolean orientateToTarget) {
            this.orientateToTarget = orientateToTarget;
        }

        public TickCount getOrientateToTargetMaxStep() {
            return orientateToTargetMaxStep;
        }
        public void setOrientateToTargetMaxStep(TickCount orientateToTargetMaxStep) {
            this.orientateToTargetMaxStep = orientateToTargetMaxStep;
        }

        public boolean isAbortOnOwnerGetDamaged() {
            return abortOnOwnerGetDamaged;
        }
        public void setAbortOnOwnerGetDamaged(boolean abortOnOwnerGetDamaged) {
            this.abortOnOwnerGetDamaged = abortOnOwnerGetDamaged;
        }

        public boolean isAbortOnModeChange() {
            return abortOnModeChange;
        }
        public void setAbortOnModeChange(boolean abortOnModeChange) {
            this.abortOnModeChange = abortOnModeChange;
        }
    }

    public static final class JobSpawnSquad implements Job {
        @JsonIgnore
        public JobType getType() {
            return JobType.SpawnSquad;
        }

    }

    public static final class JobLootTargetSquad implements Job {
        @JsonProperty(value = "target_entity_id", required = true)
        public EntityId targetEntityId;

        @JsonIgnore
        public JobType getType() {
            return JobType.LootTargetSquad;
        }

        public EntityId getTargetEntityId() {
            return targetEntityId;
        }
        public void setTargetEntityId(EntityId targetEntityId) {
            this.targetEntityId = targetEntityId;
        }
    }

    public static final class JobMorph implements Job {
        @JsonProperty(required = true)
        public TargetHolder target;
        @JsonProperty(required = true)
        public SpellId spell;

        @JsonIgnore
        public JobType getType() {
            return JobType.Morph;
        }

        public TargetHolder getTarget() {
            return target;
        }
        public void setTarget(TargetHolder target) {
            this.target = target;
        }

        public SpellId getSpell() {
            return spell;
        }
        public void setSpell(SpellId spell) {
            this.spell = spell;
        }
    }

    /** if you see this it means we did not account for some EA's case, so please report it */
    public static final class JobUnknown implements Job {
        @JsonProperty(required = true)
        public int id;

        @JsonIgnore
        public JobType getType() {
            return JobType.Morph;
        }

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
    }

    /**
     * With the way the game works, I would not be surprised, if this will cause more issues.
     * If the game crashes send the log to `Kubik`.
     * It probably mean some field in one of the `Job`s needs to be adjusted.
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
        private JobGoto goTo; // goto is reserved
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
        /** if you see this it means we did not account for some EA's case, so please report it */
        @JsonProperty(value = "Unknown")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private JobUnknown unknown;

        public Job get() {
            if (noJob != null) { return noJob; }
            else if (idle != null) { return idle; }
            else if (goTo != null) { return goTo; }
            else if (attackMelee != null) { return attackMelee; }
            else if (castSpell != null) { return castSpell; }
            else if (die != null) { return die; }
            else if (talk != null) { return talk; }
            else if (scriptTalk != null) { return scriptTalk; }
            else if (freeze != null) { return freeze; }
            else if (spawn != null) { return spawn; }
            else if (cheer != null) { return cheer; }
            else if (attackSquad != null) { return attackSquad; }
            else if (castSpellSquad != null) { return castSpellSquad; }
            else if (pushBack != null) { return pushBack; }
            else if (stampede != null) { return stampede; }
            else if (barrierCrush != null) { return barrierCrush; }
            else if (barrierGateToggle != null) { return barrierGateToggle; }
            else if (flameThrower != null) { return flameThrower; }
            else if (construct != null) { return construct; }
            else if (crush != null) { return crush; }
            else if (mountBarrierSquad != null) { return mountBarrierSquad; }
            else if (mountBarrier != null) { return mountBarrier; }
            else if (modeChangeSquad != null) { return modeChangeSquad; }
            else if (modeChange != null) { return modeChange; }
            else if (sacrificeSquad != null) { return sacrificeSquad; }
            else if (usePortalSquad != null) { return usePortalSquad; }
            else if (channel != null) { return channel; }
            else if (spawnSquad != null) { return spawnSquad; }
            else if (lootTargetSquad != null) { return lootTargetSquad; }
            else if (morph != null) { return morph; }
            else if (unknown != null) { return unknown; }
            else { throw new IllegalStateException("JobHolder doesn't contain any Job. Check implementation and API!"); }
        }
        public JobHolder() { }
        public JobHolder(Job job) {
            Objects.requireNonNull(job, "Job must not be null");
            switch (job.getType()) {
                case JobType.NoJob:
                    noJob = (JobNoJob) job;
                    break;
                case JobType.Idle:
                    idle = (JobIdle) job;
                    break;
                case JobType.Goto:
                    goTo = (JobGoto) job;
                    break;
                case JobType.AttackMelee:
                    attackMelee = (JobAttackMelee) job;
                    break;
                case JobType.CastSpell:
                    castSpell = (JobCastSpell) job;
                    break;
                case JobType.Die:
                    die = (JobDie) job;
                    break;
                case JobType.Talk:
                    talk = (JobTalk) job;
                    break;
                case JobType.ScriptTalk:
                    scriptTalk = (JobScriptTalk) job;
                    break;
                case JobType.Freeze:
                    freeze = (JobFreeze) job;
                    break;
                case JobType.Spawn:
                    spawn = (JobSpawn) job;
                    break;
                case JobType.Cheer:
                    cheer = (JobCheer) job;
                    break;
                case JobType.AttackSquad:
                    attackSquad = (JobAttackSquad) job;
                    break;
                case JobType.CastSpellSquad:
                    castSpellSquad = (JobCastSpellSquad) job;
                    break;
                case JobType.PushBack:
                    pushBack = (JobPushBack) job;
                    break;
                case JobType.Stampede:
                    stampede = (JobStampede) job;
                    break;
                case JobType.BarrierCrush:
                    barrierCrush = (JobBarrierCrush) job;
                    break;
                case JobType.BarrierGateToggle:
                    barrierGateToggle = (JobBarrierGateToggle) job;
                    break;
                case JobType.FlameThrower:
                    flameThrower = (JobFlameThrower) job;
                    break;
                case JobType.Construct:
                    construct = (JobConstruct) job;
                    break;
                case JobType.Crush:
                    crush = (JobCrush) job;
                    break;
                case JobType.MountBarrierSquad:
                    mountBarrierSquad = (JobMountBarrierSquad) job;
                    break;
                case JobType.MountBarrier:
                    mountBarrier = (JobMountBarrier) job;
                    break;
                case JobType.ModeChangeSquad:
                    modeChangeSquad = (JobModeChangeSquad) job;
                    break;
                case JobType.ModeChange:
                    modeChange = (JobModeChange) job;
                    break;
                case JobType.SacrificeSquad:
                    sacrificeSquad = (JobSacrificeSquad) job;
                    break;
                case JobType.UsePortalSquad:
                    usePortalSquad = (JobUsePortalSquad) job;
                    break;
                case JobType.Channel:
                    channel = (JobChannel) job;
                    break;
                case JobType.SpawnSquad:
                    spawnSquad = (JobSpawnSquad) job;
                    break;
                case JobType.LootTargetSquad:
                    lootTargetSquad = (JobLootTargetSquad) job;
                    break;
                case JobType.Morph:
                    morph = (JobMorph) job;
                    break;
                case JobType.Unknown:
                    unknown = (JobUnknown) job;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Job " + job.getType());
            }
        }

        public JobNoJob getNoJob() {
            return noJob;
        }
        public void setNoJob(JobNoJob noJob) {
            this.noJob = noJob;
        }

        public JobIdle getIdle() {
            return idle;
        }
        public void setIdle(JobIdle idle) {
            this.idle = idle;
        }

        public JobGoto getGoTo() {
            return goTo;
        }
        public void setGoTo(JobGoto goTo) {
            this.goTo = goTo;
        }

        public JobAttackMelee getAttackMelee() {
            return attackMelee;
        }
        public void setAttackMelee(JobAttackMelee attackMelee) {
            this.attackMelee = attackMelee;
        }

        public JobCastSpell getCastSpell() {
            return castSpell;
        }
        public void setCastSpell(JobCastSpell castSpell) {
            this.castSpell = castSpell;
        }

        public JobDie getDie() {
            return die;
        }
        public void setDie(JobDie die) {
            this.die = die;
        }

        public JobTalk getTalk() {
            return talk;
        }
        public void setTalk(JobTalk talk) {
            this.talk = talk;
        }

        public JobScriptTalk getScriptTalk() {
            return scriptTalk;
        }
        public void setScriptTalk(JobScriptTalk scriptTalk) {
            this.scriptTalk = scriptTalk;
        }

        public JobFreeze getFreeze() {
            return freeze;
        }
        public void setFreeze(JobFreeze freeze) {
            this.freeze = freeze;
        }

        public JobSpawn getSpawn() {
            return spawn;
        }
        public void setSpawn(JobSpawn spawn) {
            this.spawn = spawn;
        }

        public JobCheer getCheer() {
            return cheer;
        }
        public void setCheer(JobCheer cheer) {
            this.cheer = cheer;
        }

        public JobAttackSquad getAttackSquad() {
            return attackSquad;
        }
        public void setAttackSquad(JobAttackSquad attackSquad) {
            this.attackSquad = attackSquad;
        }

        public JobCastSpellSquad getCastSpellSquad() {
            return castSpellSquad;
        }
        public void setCastSpellSquad(JobCastSpellSquad castSpellSquad) {
            this.castSpellSquad = castSpellSquad;
        }

        public JobPushBack getPushBack() {
            return pushBack;
        }
        public void setPushBack(JobPushBack pushBack) {
            this.pushBack = pushBack;
        }

        public JobStampede getStampede() {
            return stampede;
        }
        public void setStampede(JobStampede stampede) {
            this.stampede = stampede;
        }

        public JobBarrierCrush getBarrierCrush() {
            return barrierCrush;
        }
        public void setBarrierCrush(JobBarrierCrush barrierCrush) {
            this.barrierCrush = barrierCrush;
        }

        public JobBarrierGateToggle getBarrierGateToggle() {
            return barrierGateToggle;
        }
        public void setBarrierGateToggle(JobBarrierGateToggle barrierGateToggle) {
            this.barrierGateToggle = barrierGateToggle;
        }

        public JobFlameThrower getFlameThrower() {
            return flameThrower;
        }
        public void setFlameThrower(JobFlameThrower flameThrower) {
            this.flameThrower = flameThrower;
        }

        public JobConstruct getConstruct() {
            return construct;
        }
        public void setConstruct(JobConstruct construct) {
            this.construct = construct;
        }

        public JobCrush getCrush() {
            return crush;
        }
        public void setCrush(JobCrush crush) {
            this.crush = crush;
        }

        public JobMountBarrierSquad getMountBarrierSquad() {
            return mountBarrierSquad;
        }
        public void setMountBarrierSquad(JobMountBarrierSquad mountBarrierSquad) {
            this.mountBarrierSquad = mountBarrierSquad;
        }

        public JobMountBarrier getMountBarrier() {
            return mountBarrier;
        }
        public void setMountBarrier(JobMountBarrier mountBarrier) {
            this.mountBarrier = mountBarrier;
        }

        public JobModeChangeSquad getModeChangeSquad() {
            return modeChangeSquad;
        }
        public void setModeChangeSquad(JobModeChangeSquad modeChangeSquad) {
            this.modeChangeSquad = modeChangeSquad;
        }

        public JobModeChange getModeChange() {
            return modeChange;
        }
        public void setModeChange(JobModeChange modeChange) {
            this.modeChange = modeChange;
        }

        public JobSacrificeSquad getSacrificeSquad() {
            return sacrificeSquad;
        }
        public void setSacrificeSquad(JobSacrificeSquad sacrificeSquad) {
            this.sacrificeSquad = sacrificeSquad;
        }

        public JobUsePortalSquad getUsePortalSquad() {
            return usePortalSquad;
        }
        public void setUsePortalSquad(JobUsePortalSquad usePortalSquad) {
            this.usePortalSquad = usePortalSquad;
        }

        public JobChannel getChannel() {
            return channel;
        }
        public void setChannel(JobChannel channel) {
            this.channel = channel;
        }

        public JobSpawnSquad getSpawnSquad() {
            return spawnSquad;
        }
        public void setSpawnSquad(JobSpawnSquad spawnSquad) {
            this.spawnSquad = spawnSquad;
        }

        public JobLootTargetSquad getLootTargetSquad() {
            return lootTargetSquad;
        }
        public void setLootTargetSquad(JobLootTargetSquad lootTargetSquad) {
            this.lootTargetSquad = lootTargetSquad;
        }

        public JobMorph getMorph() {
            return morph;
        }
        public void setMorph(JobMorph morph) {
            this.morph = morph;
        }

        public JobUnknown getUnknown() {
            return unknown;
        }
        public void setUnknown(JobUnknown unknown) {
            this.unknown = unknown;
        }
    }

    public enum Ping {
        Attention(0),
        Attack(1),
        Defend(2),
        NeedHelp(4),
        Meet(5);

        //----------------------------------------
        public final int value;
        Ping(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        public static Optional<Ping> fromValue(int value) {
            return Arrays.stream(Ping.values())
                    .filter(p -> p.value == value)
                    .findFirst();
        }
    }

    /** Entity on the map */
    public static class Entity {
        /** Unique id of the entity */
        @JsonProperty(required = true)
        private EntityId id;
        /** List of effects the entity have. */
        @JsonProperty(required = true)
        private List<AbilityEffect> effects;
        /** List of aspects entity have. */
        @JsonProperty(required = true)
        private List<AspectHolder> aspects;
        /** What is the entity doing right now */
        @JsonProperty(required = true)
        private JobHolder job;
        /** position on the map */
        @JsonProperty(required = true)
        private Position position;
        /** id of player that owns this entity */
        @JsonProperty(value = "player_entity_id")
        public EntityId playerEntityId;

        public EntityId getId() {
            return id;
        }
        public void setId(EntityId id) {
            this.id = id;
        }

        public List<AbilityEffect> getEffects() {
            return effects;
        }
        public void setEffects(List<AbilityEffect> effects) {
            this.effects = effects;
        }

        public List<AspectHolder> getAspects() {
            return aspects;
        }
        public void setAspects(List<AspectHolder> aspects) {
            this.aspects = aspects;
        }

        public JobHolder getJob() {
            return job;
        }
        public void setJob(JobHolder job) {
            this.job = job;
        }

        public Position getPosition() {
            return position;
        }
        public void setPosition(Position position) {
            this.position = position;
        }

        public EntityId getPlayerEntityId() {
            return playerEntityId;
        }
        public void setPlayerEntityId(EntityId playerEntityId) {
            this.playerEntityId = playerEntityId;
        }
    }

    public static class Projectile {
        /**  Unique id of the entity */
        @JsonProperty(required = true)
        private EntityId id;
        /**  position on the map */
        @JsonProperty(required = true)
        private Position position;

        public EntityId getId() {
            return id;
        }
        public void setId(EntityId id) {
            this.id = id;
        }

        public Position getPosition() {
            return position;
        }
        public void setPosition(Position position) {
            this.position = position;
        }
    }

    public static class PowerSlot  {
        @JsonProperty(required = true)
        private Entity entity;
        @JsonProperty(value = "res_id", required = true)
        private int resId;
        @JsonProperty(required = true)
        private int state;
        @JsonProperty(required = true)
        private byte team;

        public Entity getEntity() {
            return entity;
        }
        public void setEntity(Entity entity) {
            this.entity = entity;
        }

        public int getResId() {
            return resId;
        }
        public void setResId(int resId) {
            this.resId = resId;
        }

        public int getState() {
            return state;
        }
        public void setState(int state) {
            this.state = state;
        }

        public byte getTeam() {
            return team;
        }
        public void setTeam(byte team) {
            this.team = team;
        }
    }

    public static class TokenSlot  {
        @JsonProperty(required = true)
        private Entity entity;
        @JsonProperty(required = true)
        private OrbColor color;

        public Entity getEntity() {
            return entity;
        }
        public void setEntity(Entity entity) {
            this.entity = entity;
        }

        public OrbColor getColor() {
            return color;
        }
        public void setColor(OrbColor color) {
            this.color = color;
        }
    }

    public static class AbilityWorldObject  {
        @JsonProperty(required = true)
        private Entity entity;

        public Entity getEntity() {
            return entity;
        }
        public void setEntity(Entity entity) {
            this.entity = entity;
        }
    }

    public static class Squad {
        @JsonProperty(required = true)
        private Entity entity;
        /** automatically transformed from id to Card and back during JSON serializing and deserializing */
        @JsonProperty(value = "card_id", required = true)
        private Card card;
        @JsonProperty(value = "res_squad_id", required = true)
        private SquadId resSquadId;
        @JsonProperty(value = "bound_power", required = true)
        private float boundPower;
        @JsonProperty(value = "squad_size", required = true)
        private byte squadSize;
        /** IDs of the figures in the squad */
        @JsonProperty(required = true)
        private List<EntityId> figures;

        public Entity getEntity() {
            return entity;
        }
        public void setEntity(Entity entity) {
            this.entity = entity;
        }

        public Card getCard() {
            return card;
        }
        public void setCard(Card card) {
            this.card = card;
        }

        public SquadId getResSquadId() {
            return resSquadId;
        }
        public void setResSquadId(SquadId resSquadId) {
            this.resSquadId = resSquadId;
        }

        public float getBoundPower() {
            return boundPower;
        }
        public void setBoundPower(float boundPower) {
            this.boundPower = boundPower;
        }

        public byte getSquadSize() {
            return squadSize;
        }
        public void setSquadSize(byte squadSize) {
            this.squadSize = squadSize;
        }

        public List<EntityId> getFigures() {
            return figures;
        }
        public void setFigures(List<EntityId> figures) {
            this.figures = figures;
        }
    }

    public static class Figure {
        @JsonProperty(required = true)
        private Entity entity;
        @JsonProperty(value = "squad_id", required = true)
        private EntityId squadId;
        @JsonProperty(value = "current_speed", required = true)
        private float currentSpeed;
        @JsonProperty(value = "rotation_speed", required = true)
        private float rotationSpeed;
        @JsonProperty(value = "unit_size", required = true)
        private byte unitSize;
        @JsonProperty(value = "move_mode", required = true)
        private byte moveMode;

        public Entity getEntity() {
            return entity;
        }
        public void setEntity(Entity entity) {
            this.entity = entity;
        }

        public EntityId getSquadId() {
            return squadId;
        }
        public void setSquadId(EntityId squadId) {
            this.squadId = squadId;
        }

        public float getCurrentSpeed() {
            return currentSpeed;
        }
        public void setCurrentSpeed(float currentSpeed) {
            this.currentSpeed = currentSpeed;
        }

        public float getRotationSpeed() {
            return rotationSpeed;
        }
        public void setRotationSpeed(float rotationSpeed) {
            this.rotationSpeed = rotationSpeed;
        }

        public byte getUnitSize() {
            return unitSize;
        }
        public void setUnitSize(byte unitSize) {
            this.unitSize = unitSize;
        }

        public byte getMoveMode() {
            return moveMode;
        }
        public void setMoveMode(byte moveMode) {
            this.moveMode = moveMode;
        }
    }

    public static class Building {
        @JsonProperty(required = true)
        private Entity entity;
        @JsonProperty(value = "building_id", required = true)
        private BuildingId buildingId;
        /** automatically transformed from id to Card and back during JSON serializing and deserializing */
        @JsonProperty(value = "card_id", required = true)
        private Card card;
        @JsonProperty(value = "power_cost", required = true)
        private float powerCost;

        public Entity getEntity() {
            return entity;
        }
        public void setEntity(Entity entity) {
            this.entity = entity;
        }

        public BuildingId getBuildingId() {
            return buildingId;
        }
        public void setBuildingId(BuildingId buildingId) {
            this.buildingId = buildingId;
        }

        public Card getCard() {
            return card;
        }
        public void setCard(Card card) {
            this.card = card;
        }

        public float getPowerCost() {
            return powerCost;
        }
        public void setPowerCost(float powerCost) {
            this.powerCost = powerCost;
        }
    }

    public static class BarrierSet {
        @JsonProperty(required = true)
        private Entity entity;

        public Entity getEntity() {
            return entity;
        }
        public void setEntity(Entity entity) {
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
        @JsonProperty(value = "free_slots", required = true)
        private byte FreeSlots;
        @JsonProperty(required = true)
        private boolean walkable;

        public Entity getEntity() {
            return entity;
        }
        public void setEntity(Entity entity) {
            this.entity = entity;
        }

        public byte getTeam() {
            return team;
        }
        public void setTeam(byte team) {
            this.team = team;
        }

        public EntityId getSet() {
            return set;
        }
        public void setSet(EntityId set) {
            this.set = set;
        }

        public int getState() {
            return state;
        }
        public void setState(int state) {
            this.state = state;
        }

        public byte getSlots() {
            return slots;
        }
        public void setSlots(byte slots) {
            this.slots = slots;
        }

        public byte getFreeSlots() {
            return FreeSlots;
        }
        public void setFreeSlots(byte freeSlots) {
            FreeSlots = freeSlots;
        }

        public boolean isWalkable() {
            return walkable;
        }
        public void setWalkable(boolean walkable) {
            this.walkable = walkable;
        }
    }

    public static class MapEntities {
        @JsonProperty(required = true)
        private List<Projectile> projectiles;
        @JsonProperty(value = "power_slots", required = true)
        private List<PowerSlot> powerSlots;
        @JsonProperty(value = "token_slots", required = true)
        private List<TokenSlot> tokenSlots;
        @JsonProperty(value = "ability_world_objects", required = true)
        private List<AbilityWorldObject> abilityWorldObjects;
        @JsonProperty(required = true)
        private List<Squad> squads;
        @JsonProperty(required = true)
        private List<Figure> figures;
        @JsonProperty(required = true)
        private List<Building> buildings;
        @JsonProperty(value = "barrier_sets", required = true)
        private List<BarrierSet> barrierSets;
        @JsonProperty(value = "barrier_modules", required = true)
        private List<BarrierModule> barrierModules;

        public List<Projectile> getProjectiles() {
            return projectiles;
        }
        public void setProjectiles(List<Projectile> projectiles) {
            this.projectiles = projectiles;
        }

        public List<PowerSlot> getPowerSlots() {
            return powerSlots;
        }
        public void setPowerSlots(List<PowerSlot> powerSlots) {
            this.powerSlots = powerSlots;
        }

        public List<TokenSlot> getTokenSlots() {
            return tokenSlots;
        }
        public void setTokenSlots(List<TokenSlot> tokenSlots) {
            this.tokenSlots = tokenSlots;
        }

        public List<AbilityWorldObject> getAbilityWorldObjects() {
            return abilityWorldObjects;
        }
        public void setAbilityWorldObjects(List<AbilityWorldObject> abilityWorldObjects) {
            this.abilityWorldObjects = abilityWorldObjects;
        }

        public List<Squad> getSquads() {
            return squads;
        }
        public void setSquads(List<Squad> squads) {
            this.squads = squads;
        }

        public List<Figure> getFigures() {
            return figures;
        }
        public void setFigures(List<Figure> figures) {
            this.figures = figures;
        }

        public List<Building> getBuildings() {
            return buildings;
        }
        public void setBuildings(List<Building> buildings) {
            this.buildings = buildings;
        }

        public List<BarrierSet> getBarrierSets() {
            return barrierSets;
        }
        public void setBarrierSets(List<BarrierSet> barrierSets) {
            this.barrierSets = barrierSets;
        }

        public List<BarrierModule> getBarrierModules() {
            return barrierModules;
        }
        public void setBarrierModules(List<BarrierModule> barrierModules) {
            this.barrierModules = barrierModules;
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
        Ping,
        Surrender,
        WhisperToMaster;
    }

    public interface Command extends MultiType<CommandType> {
    }

    /** Play card of building type. */
    public static final class CommandBuildHouse implements Command {
        @JsonProperty(value = "card_position", required = true)
        private byte cardPosition;
        @JsonProperty(required = true)
        private Position2D xy;
        @JsonProperty(required = true)
        private float angle;

        public CommandBuildHouse() {}
        public CommandBuildHouse(byte cardPosition, Position2D xy, float angle) {
            this.cardPosition = cardPosition;
            this.xy = xy;
            this.angle = angle;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.BuildHouse;
        }

        public byte getCardPosition() {
            return cardPosition;
        }
        public void setCardPosition(byte cardPosition) {
            this.cardPosition = cardPosition;
        }

        public Position2D getXy() {
            return xy;
        }
        public void setXy(Position2D xy) {
            this.xy = xy;
        }

        public float getAngle() {
            return angle;
        }
        public void setAngle(float angle) {
            this.angle = angle;
        }
    }

    /** Play card of Spell type. (single target) */
    public static final class CommandCastSpellGod implements Command {
        @JsonProperty(value = "card_position", required = true)
        private byte cardPosition;
        @JsonProperty(required = true)
        private SingleTargetHolder target;

        public CommandCastSpellGod() {}
        public CommandCastSpellGod(byte cardPosition, SingleTargetHolder target) {
            this.cardPosition = cardPosition;
            this.target = target;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.CastSpellGod;
        }

        public byte getCardPosition() {
            return cardPosition;
        }
        public void setCardPosition(byte cardPosition) {
            this.cardPosition = cardPosition;
        }

        public SingleTargetHolder getTarget() {
            return target;
        }
        public void setTarget(SingleTargetHolder target) {
            this.target = target;
        }
    }

    /** Play card of Spell type. (line target) */
    public static final class CommandCastSpellGodMulti implements Command {
        @JsonProperty(value = "card_position", required = true)
        private byte cardPosition;
        @JsonProperty(required = true)
        private Position2D xy1;
        @JsonProperty(required = true)
        private Position2D xy2;

        public CommandCastSpellGodMulti() {}
        public CommandCastSpellGodMulti(byte cardPosition, Position2D xy1, Position2D xy2) {
            this.cardPosition = cardPosition;
            this.xy1 = xy1;
            this.xy2 = xy2;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.CastSpellGodMulti;
        }

        public byte getCardPosition() {
            return cardPosition;
        }
        public void setCardPosition(byte cardPosition) {
            this.cardPosition = cardPosition;
        }

        public Position2D getXy1() {
            return xy1;
        }
        public void setXy1(Position2D xy1) {
            this.xy1 = xy1;
        }

        public Position2D getXy2() {
            return xy2;
        }
        public void setXy2(Position2D xy2) {
            this.xy2 = xy2;
        }
    }

    /** Play card of squad type (on ground) */
    public static final class CommandProduceSquad implements Command {
        @JsonProperty(value = "card_position", required = true)
        private byte CardPosition;
        @JsonProperty(required = true)
        private Position2D xy;

        public CommandProduceSquad() {}
        public CommandProduceSquad(byte cardPosition, Position2D xy) {
            CardPosition = cardPosition;
            this.xy = xy;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.ProduceSquad;
        }

        public byte getCardPosition() {
            return CardPosition;
        }
        public void setCardPosition(byte cardPosition) {
            CardPosition = cardPosition;
        }

        public Position2D getXy() {
            return xy;
        }
        public void setXy(Position2D xy) {
            this.xy = xy;
        }
    }

    /** Play card of squad type (on barrier) */
    public static final class CommandProduceSquadOnBarrier implements Command {
        @JsonProperty(value = "card_position", required = true)
        private byte cardPosition;
        @JsonProperty(required = true)
        private Position2D xy;
        @JsonProperty(value = "barrier_to_mount", required = true)
        private EntityId barrierToMount;

        public CommandProduceSquadOnBarrier() {}
        public CommandProduceSquadOnBarrier(byte cardPosition, Position2D xy, EntityId barrierToMount) {
            this.cardPosition = cardPosition;
            this.xy = xy;
            this.barrierToMount = barrierToMount;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.ProduceSquadOnBarrier;
        }

        public byte getCardPosition() {
            return cardPosition;
        }
        public void setCardPosition(byte cardPosition) {
            this.cardPosition = cardPosition;
        }

        public Position2D getXy() {
            return xy;
        }
        public void setXy(Position2D xy) {
            this.xy = xy;
        }

        public EntityId getBarrierToMount() {
            return barrierToMount;
        }
        public void setBarrierToMount(EntityId barrierToMount) {
            this.barrierToMount = barrierToMount;
        }
    }

    /** Activates spell or ability on entity. */
    public static final class CommandCastSpellEntity implements Command {
        @JsonProperty(required = true)
        private EntityId entity;
        @JsonProperty(required = true)
        private SpellId spell;
        @JsonProperty(required = true)
        private SingleTargetHolder target;

        public CommandCastSpellEntity() {}
        public CommandCastSpellEntity(EntityId entity, SpellId spell, SingleTargetHolder target) {
            this.entity = entity;
            this.spell = spell;
            this.target = target;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.CastSpellEntity;
        }

        public EntityId getEntity() {
            return entity;
        }
        public void setEntity(EntityId entity) {
            this.entity = entity;
        }

        public SpellId getSpell() {
            return spell;
        }
        public void setSpell(SpellId spell) {
            this.spell = spell;
        }

        public SingleTargetHolder getTarget() {
            return target;
        }
        public void setTarget(SingleTargetHolder target) {
            this.target = target;
        }
    }

    /** Opens or closes gate. */
    public static final class CommandBarrierGateToggle implements Command {
        @JsonProperty(value = "barrier_id", required = true)
        private EntityId barrierId;

        public CommandBarrierGateToggle() {}
        public CommandBarrierGateToggle(EntityId barrierId) {
            this.barrierId = barrierId;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.BarrierGateToggle;
        }

        public EntityId getBarrierId() {
            return barrierId;
        }
        public void setBarrierId(EntityId barrierId) {
            this.barrierId = barrierId;
        }
    }

    /** Build barrier. (same as BarrierRepair if not inverted) */
    public static final class CommandBarrierBuild implements Command {
        @JsonProperty(value = "barrier_id", required = true)
        private EntityId barrierId;
        @JsonProperty(value = "inverted_direction", required = true)
        private boolean invertedDirection;

        public CommandBarrierBuild() {}
        public CommandBarrierBuild(EntityId barrierId, boolean invertedDirection) {
            this.barrierId = barrierId;
            this.invertedDirection = invertedDirection;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.BarrierBuild;
        }

        public EntityId getBarrierId() {
            return barrierId;
        }
        public void setBarrierId(EntityId barrierId) {
            this.barrierId = barrierId;
        }

        public boolean isInvertedDirection() {
            return invertedDirection;
        }
        public void setInvertedDirection(boolean invertedDirection) {
            this.invertedDirection = invertedDirection;
        }
    }

    /** Repair barrier. */
    public static final class CommandBarrierRepair implements Command {
        @JsonProperty(value = "barrier_id", required = true)
        private EntityId barrierId;

        public CommandBarrierRepair() {}
        public CommandBarrierRepair(EntityId barrierId) {
            this.barrierId = barrierId;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.BarrierRepair;
        }

        public EntityId getBarrierId() {
            return barrierId;
        }
        public void setBarrierId(EntityId barrierId) {
            this.barrierId = barrierId;
        }
    }

    public static final class CommandBarrierCancelRepair implements Command {
        @JsonProperty(value = "barrier_id", required = true)
        private EntityId barrierId;

        public CommandBarrierCancelRepair() {}
        public CommandBarrierCancelRepair(EntityId barrierId) {
            this.barrierId = barrierId;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.BarrierCancelRepair;
        }

        public EntityId getBarrierId() {
            return barrierId;
        }
        public void setBarrierId(EntityId barrierId) {
            this.barrierId = barrierId;
        }
    }

    public static final class CommandRepairBuilding implements Command {
        @JsonProperty(value = "building_id", required = true)
        private EntityId buildingId;

        public CommandRepairBuilding() {}
        public CommandRepairBuilding(EntityId buildingId) {
            this.buildingId = buildingId;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.RepairBuilding;
        }

        public EntityId getBuildingId() {
            return buildingId;
        }
        public void setBuildingId(EntityId buildingId) {
            this.buildingId = buildingId;
        }
    }

    public static final class CommandCancelRepairBuilding implements Command {
        @JsonProperty(value = "building_id", required = true)
        private EntityId buildingId;

        public CommandCancelRepairBuilding() {}
        public CommandCancelRepairBuilding(EntityId buildingId) {
            this.buildingId = buildingId;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.CancelRepairBuilding;
        }

        public EntityId getBuildingId() {
            return buildingId;
        }
        public void setBuildingId(EntityId buildingId) {
            this.buildingId = buildingId;
        }
    }

    public static final class CommandGroupAttack implements Command {
        @JsonProperty(required = true)
        private List<EntityId> squads;
        @JsonProperty(value = "target_entity_id", required = true)
        private EntityId targetEntityId;
        @JsonProperty(value = "force_attack", required = true)
        private boolean forceAttack;

        public CommandGroupAttack() {}
        public CommandGroupAttack(List<EntityId> squads, EntityId targetEntityId, boolean forceAttack) {
            this.squads = squads;
            this.targetEntityId = targetEntityId;
            this.forceAttack = forceAttack;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.GroupAttack;
        }

        public List<EntityId> getSquads() {
            return squads;
        }
        public void setSquads(List<EntityId> squads) {
            this.squads = squads;
        }

        public EntityId getTargetEntityId() {
            return targetEntityId;
        }
        public void setTargetEntityId(EntityId targetEntityId) {
            this.targetEntityId = targetEntityId;
        }

        public boolean isForceAttack() {
            return forceAttack;
        }
        public void setForceAttack(boolean forceAttack) {
            this.forceAttack = forceAttack;
        }
    }

    public static final class CommandGroupEnterWall implements Command {
        @JsonProperty(required = true)
        private List<EntityId> squads;
        @JsonProperty(value = "barrier_id", required = true)
        private EntityId barrierId;

        public CommandGroupEnterWall() {}
        public CommandGroupEnterWall(List<EntityId> squads, EntityId barrierId) {
            this.squads = squads;
            this.barrierId = barrierId;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.GroupEnterWall;
        }

        public List<EntityId> getSquads() {
            return squads;
        }
        public void setSquads(List<EntityId> squads) {
            this.squads = squads;
        }

        public EntityId getBarrierId() {
            return barrierId;
        }
        public void setBarrierId(EntityId barrierId) {
            this.barrierId = barrierId;
        }
    }

    public static final class CommandGroupExitWall implements Command {
        @JsonProperty(required = true)
        private List<EntityId> squads;
        @JsonProperty(value = "barrier_id", required = true)
        private EntityId barrierId;

        public CommandGroupExitWall() {}
        public CommandGroupExitWall(List<EntityId> squads, EntityId barrierId) {
            this.squads = squads;
            this.barrierId = barrierId;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.GroupExitWall;
        }

        public List<EntityId> getSquads() {
            return squads;
        }
        public void setSquads(List<EntityId> squads) {
            this.squads = squads;
        }

        public EntityId getBarrierId() {
            return barrierId;
        }
        public void setBarrierId(EntityId barrierId) {
            this.barrierId = barrierId;
        }
    }

    public static final class CommandGroupGoto implements Command {
        @JsonProperty(required = true)
        private List<EntityId> squads;
        @JsonProperty(required = true)
        private List<Position2D> positions;
        @JsonProperty(value = "walk_mode", required = true)
        private WalkMode walkMode;
        @JsonProperty(required = true)
        private float orientation;

        public CommandGroupGoto() {}
        public CommandGroupGoto(List<EntityId> squads, List<Position2D> positions, WalkMode walkMode, float orientation) {
            this.squads = squads;
            this.positions = positions;
            this.walkMode = walkMode;
            this.orientation = orientation;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.GroupGoto;
        }

        public List<EntityId> getSquads() {
            return squads;
        }
        public void setSquads(List<EntityId> squads) {
            this.squads = squads;
        }

        public List<Position2D> getPositions() {
            return positions;
        }
        public void setPositions(List<Position2D> positions) {
            this.positions = positions;
        }

        public WalkMode getWalkMode() {
            return walkMode;
        }
        public void setWalkMode(WalkMode walkMode) {
            this.walkMode = walkMode;
        }

        public float getOrientation() {
            return orientation;
        }
        public void setOrientation(float orientation) {
            this.orientation = orientation;
        }
    }

    public static final class CommandGroupHoldPosition implements Command {
        @JsonProperty(required = true)
        private List<EntityId> squads;

        public CommandGroupHoldPosition() {}
        public CommandGroupHoldPosition(List<EntityId> squads) {
            this.squads = squads;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.GroupHoldPosition;
        }

        public List<EntityId> getSquads() {
            return squads;
        }
        public void setSquads(List<EntityId> squads) {
            this.squads = squads;
        }
    }

    public static final class CommandGroupStopJob implements Command {
        @JsonProperty(required = true)
        private List<EntityId> squads;

        public CommandGroupStopJob() {}
        public CommandGroupStopJob(List<EntityId> squads) {
            this.squads = squads;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.GroupStopJob;
        }

        public List<EntityId> getSquads() {
            return squads;
        }
        public void setSquads(List<EntityId> squads) {
            this.squads = squads;
        }
    }

    public static final class CommandModeChange implements Command {
        @JsonProperty(value = "entity_id", required = true)
        private EntityId entityId;
        @JsonProperty(value = "new_mode_id", required = true)
        private ModeId newModeId;

        public CommandModeChange() {}
        public CommandModeChange(EntityId entityId, ModeId newModeId) {
            this.entityId = entityId;
            this.newModeId = newModeId;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.ModeChange;
        }

        public EntityId getEntityId() {
            return entityId;
        }
        public void setEntityId(EntityId entityId) {
            this.entityId = entityId;
        }

        public ModeId getNewModeId() {
            return newModeId;
        }
        public void setNewModeId(ModeId newModeId) {
            this.newModeId = newModeId;
        }
    }

    public static final class CommandPowerSlotBuild implements Command {
        @JsonProperty(value = "slot_id", required = true)
        private EntityId slotId;

        public CommandPowerSlotBuild() {}
        public CommandPowerSlotBuild(EntityId slotId) {
            this.slotId = slotId;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.PowerSlotBuild;
        }

        public EntityId getSlotId() {
            return slotId;
        }
        public void setSlotId(EntityId slotId) {
            this.slotId = slotId;
        }
    }

    public static final class CommandTokenSlotBuild implements Command {
        @JsonProperty(value = "slot_id", required = true)
        private EntityId slotId;
        @JsonProperty(required = true)
        private CreateOrbColor color;

        public CommandTokenSlotBuild() {}
        public CommandTokenSlotBuild(EntityId slotId, CreateOrbColor color) {
            this.slotId = slotId;
            this.color = color;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.TokenSlotBuild;
        }

        public EntityId getSlotId() {
            return slotId;
        }
        public void setSlotId(EntityId slotId) {
            this.slotId = slotId;
        }

        public CreateOrbColor getColor() {
            return color;
        }
        public void setColor(CreateOrbColor color) {
            this.color = color;
        }
    }

    public static final class CommandPing implements Command {
        @JsonProperty(required = true)
        private Position2D xy;
        @JsonProperty(required = true)
        private Ping ping;

        public CommandPing() {}
        public CommandPing(Position2D xy, Ping ping) {
            this.xy = xy;
            this.ping = ping;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.Ping;
        }

        public Position2D getXy() {
            return xy;
        }
        public void setXy(Position2D xy) {
            this.xy = xy;
        }

        public Ping getPing() {
            return ping;
        }
        public void setPing(Ping ping) {
            this.ping = ping;
        }
    }

    public static final class CommandSurrender implements Command {
        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.Surrender;
        }
    }

    public static final class CommandWhisperToMaster implements Command {
        @JsonProperty(required = true)
        private String text;

        public CommandWhisperToMaster() {}
        public CommandWhisperToMaster(String text) {
            this.text = text;
        }

        @Override
        @JsonIgnore
        public CommandType getType() {
            return CommandType.WhisperToMaster;
        }

        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * All the different commands a bot can issue.
     * For spectating bots all commands except Ping and WhisperToMaster are ignored.
     */
    public static class CommandHolder {
        /** Play card of building type. */
        @JsonProperty(value = "BuildHouse")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandBuildHouse buildHouse;
        /** Play card of Spell type. (single target) */
        @JsonProperty(value = "CastSpellGod")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandCastSpellGod castSpellGod;
        /** Play card of Spell type. (line target) */
        @JsonProperty(value = "CastSpellGodMulti")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandCastSpellGodMulti castSpellGodMulti;
        /** Play card of squad type (on ground) */
        @JsonProperty(value = "ProduceSquad")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandProduceSquad produceSquad;
        /** Play card of squad type (on barrier) */
        @JsonProperty(value = "ProduceSquadOnBarrier")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandProduceSquadOnBarrier produceSquadOnBarrier;
        /** Activates spell or ability on entity. */
        @JsonProperty(value = "CastSpellEntity")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandCastSpellEntity castSpellEntity;
        /** Opens or closes gate. */
        @JsonProperty(value = "BarrierGateToggle")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandBarrierGateToggle barrierGateToggle;
        /** Build barrier. (same as BarrierRepair if not inverted) */
        @JsonProperty(value = "BarrierBuild")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandBarrierBuild barrierBuild;
        /** Repair barrier. */
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
        @JsonProperty(value = "Ping")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandPing ping;
        @JsonProperty(value = "Surrender")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandSurrender surrender;
        @JsonProperty(value = "WhisperToMaster")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandWhisperToMaster whisperToMaster;

        public Command get() {
            if (buildHouse != null) { return buildHouse; }
            else if (castSpellGod != null) { return castSpellGod; }
            else if (castSpellGodMulti != null) { return castSpellGodMulti; }
            else if (produceSquad != null) { return produceSquad; }
            else if (produceSquadOnBarrier != null) { return produceSquadOnBarrier; }
            else if (castSpellEntity != null) { return castSpellEntity; }
            else if (barrierGateToggle != null) { return barrierGateToggle; }
            else if (barrierBuild != null) { return barrierBuild; }
            else if (barrierRepair != null) { return barrierRepair; }
            else if (barrierCancelRepair != null) { return barrierCancelRepair; }
            else if (repairBuilding != null) { return repairBuilding; }
            else if (cancelRepairBuilding != null) { return cancelRepairBuilding; }
            else if (groupAttack != null) { return groupAttack; }
            else if (groupEnterWall != null) { return groupEnterWall; }
            else if (groupExitWall != null) { return groupExitWall; }
            else if (groupGoto != null) { return groupGoto; }
            else if (groupHoldPosition != null) { return groupHoldPosition; }
            else if (groupStopJob != null) { return groupStopJob; }
            else if (modeChange != null) { return modeChange; }
            else if (powerSlotBuild != null) { return powerSlotBuild; }
            else if (tokenSlotBuild != null) { return tokenSlotBuild; }
            else if (ping != null) { return ping; }
            else if (surrender != null) { return surrender; }
            else if (whisperToMaster != null) { return whisperToMaster; }
            else { throw new IllegalStateException("CommandHolder doesn't contain any Command. Check implementation and API!"); }
        }
        public CommandHolder() { }
        public CommandHolder(Command command) {
            Objects.requireNonNull(command, "Command must not be null");
            switch (command.getType()) {
                case CommandType.BuildHouse:
                    buildHouse = (CommandBuildHouse) command;
                    break;
                case CommandType.CastSpellGod:
                    castSpellGod = (CommandCastSpellGod) command;
                    break;
                case CommandType.CastSpellGodMulti:
                    castSpellGodMulti = (CommandCastSpellGodMulti) command;
                    break;
                case CommandType.ProduceSquad:
                    produceSquad = (CommandProduceSquad) command;
                    break;
                case CommandType.ProduceSquadOnBarrier:
                    produceSquadOnBarrier = (CommandProduceSquadOnBarrier) command;
                    break;
                case CommandType.CastSpellEntity:
                    castSpellEntity = (CommandCastSpellEntity) command;
                    break;
                case CommandType.BarrierGateToggle:
                    barrierGateToggle = (CommandBarrierGateToggle) command;
                    break;
                case CommandType.BarrierBuild:
                    barrierBuild = (CommandBarrierBuild) command;
                    break;
                case CommandType.BarrierRepair:
                    barrierRepair = (CommandBarrierRepair) command;
                    break;
                case CommandType.BarrierCancelRepair:
                    barrierCancelRepair = (CommandBarrierCancelRepair) command;
                    break;
                case CommandType.RepairBuilding:
                    repairBuilding = (CommandRepairBuilding) command;
                    break;
                case CommandType.CancelRepairBuilding:
                    cancelRepairBuilding = (CommandCancelRepairBuilding) command;
                    break;
                case CommandType.GroupAttack:
                    groupAttack = (CommandGroupAttack) command;
                    break;
                case CommandType.GroupEnterWall:
                    groupEnterWall = (CommandGroupEnterWall) command;
                    break;
                case CommandType.GroupExitWall:
                    groupExitWall = (CommandGroupExitWall) command;
                    break;
                case CommandType.GroupGoto:
                    groupGoto = (CommandGroupGoto) command;
                    break;
                case CommandType.GroupHoldPosition:
                    groupHoldPosition = (CommandGroupHoldPosition) command;
                    break;
                case CommandType.GroupStopJob:
                    groupStopJob = (CommandGroupStopJob) command;
                    break;
                case CommandType.ModeChange:
                    modeChange = (CommandModeChange) command;
                    break;
                case CommandType.PowerSlotBuild:
                    powerSlotBuild = (CommandPowerSlotBuild) command;
                    break;
                case CommandType.TokenSlotBuild:
                    tokenSlotBuild = (CommandTokenSlotBuild) command;
                    break;
                case CommandType.Ping:
                    ping = (CommandPing) command;
                    break;
                case CommandType.Surrender:
                    surrender = (CommandSurrender) command;
                    break;
                case CommandType.WhisperToMaster:
                    whisperToMaster = (CommandWhisperToMaster) command;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Command " + command.getType());
            }
        }

        public CommandBuildHouse getBuildHouse() {
            return buildHouse;
        }
        public void setBuildHouse(CommandBuildHouse buildHouse) {
            this.buildHouse = buildHouse;
        }

        public CommandCastSpellGod getCastSpellGod() {
            return castSpellGod;
        }
        public void setCastSpellGod(CommandCastSpellGod castSpellGod) {
            this.castSpellGod = castSpellGod;
        }

        public CommandCastSpellGodMulti getCastSpellGodMulti() {
            return castSpellGodMulti;
        }
        public void setCastSpellGodMulti(CommandCastSpellGodMulti castSpellGodMulti) {
            this.castSpellGodMulti = castSpellGodMulti;
        }

        public CommandProduceSquad getProduceSquad() {
            return produceSquad;
        }
        public void setProduceSquad(CommandProduceSquad produceSquad) {
            this.produceSquad = produceSquad;
        }

        public CommandProduceSquadOnBarrier getProduceSquadOnBarrier() {
            return produceSquadOnBarrier;
        }
        public void setProduceSquadOnBarrier(CommandProduceSquadOnBarrier produceSquadOnBarrier) {
            this.produceSquadOnBarrier = produceSquadOnBarrier;
        }

        public CommandCastSpellEntity getCastSpellEntity() {
            return castSpellEntity;
        }
        public void setCastSpellEntity(CommandCastSpellEntity castSpellEntity) {
            this.castSpellEntity = castSpellEntity;
        }

        public CommandBarrierGateToggle getBarrierGateToggle() {
            return barrierGateToggle;
        }
        public void setBarrierGateToggle(CommandBarrierGateToggle barrierGateToggle) {
            this.barrierGateToggle = barrierGateToggle;
        }

        public CommandBarrierBuild getBarrierBuild() {
            return barrierBuild;
        }
        public void setBarrierBuild(CommandBarrierBuild barrierBuild) {
            this.barrierBuild = barrierBuild;
        }

        public CommandBarrierRepair getBarrierRepair() {
            return barrierRepair;
        }
        public void setBarrierRepair(CommandBarrierRepair barrierRepair) {
            this.barrierRepair = barrierRepair;
        }

        public CommandBarrierCancelRepair getBarrierCancelRepair() {
            return barrierCancelRepair;
        }
        public void setBarrierCancelRepair(CommandBarrierCancelRepair barrierCancelRepair) {
            this.barrierCancelRepair = barrierCancelRepair;
        }

        public CommandRepairBuilding getRepairBuilding() {
            return repairBuilding;
        }
        public void setRepairBuilding(CommandRepairBuilding repairBuilding) {
            this.repairBuilding = repairBuilding;
        }

        public CommandCancelRepairBuilding getCancelRepairBuilding() {
            return cancelRepairBuilding;
        }
        public void setCancelRepairBuilding(CommandCancelRepairBuilding cancelRepairBuilding) {
            this.cancelRepairBuilding = cancelRepairBuilding;
        }

        public CommandGroupAttack getGroupAttack() {
            return groupAttack;
        }
        public void setGroupAttack(CommandGroupAttack groupAttack) {
            this.groupAttack = groupAttack;
        }

        public CommandGroupEnterWall getGroupEnterWall() {
            return groupEnterWall;
        }
        public void setGroupEnterWall(CommandGroupEnterWall groupEnterWall) {
            this.groupEnterWall = groupEnterWall;
        }

        public CommandGroupExitWall getGroupExitWall() {
            return groupExitWall;
        }
        public void setGroupExitWall(CommandGroupExitWall groupExitWall) {
            this.groupExitWall = groupExitWall;
        }

        public CommandGroupGoto getGroupGoto() {
            return groupGoto;
        }
        public void setGroupGoto(CommandGroupGoto groupGoto) {
            this.groupGoto = groupGoto;
        }

        public CommandGroupHoldPosition getGroupHoldPosition() {
            return groupHoldPosition;
        }
        public void setGroupHoldPosition(CommandGroupHoldPosition groupHoldPosition) {
            this.groupHoldPosition = groupHoldPosition;
        }

        public CommandGroupStopJob getGroupStopJob() {
            return groupStopJob;
        }
        public void setGroupStopJob(CommandGroupStopJob groupStopJob) {
            this.groupStopJob = groupStopJob;
        }

        public CommandModeChange getModeChange() {
            return modeChange;
        }
        public void setModeChange(CommandModeChange modeChange) {
            this.modeChange = modeChange;
        }

        public CommandPowerSlotBuild getPowerSlotBuild() {
            return powerSlotBuild;
        }
        public void setPowerSlotBuild(CommandPowerSlotBuild powerSlotBuild) {
            this.powerSlotBuild = powerSlotBuild;
        }

        public CommandTokenSlotBuild getTokenSlotBuild() {
            return tokenSlotBuild;
        }
        public void setTokenSlotBuild(CommandTokenSlotBuild tokenSlotBuild) {
            this.tokenSlotBuild = tokenSlotBuild;
        }

        public CommandPing getPing() {
            return ping;
        }
        public void setPing(CommandPing ping) {
            this.ping = ping;
        }

        public CommandSurrender getSurrender() {
            return surrender;
        }
        public void setSurrender(CommandSurrender surrender) {
            this.surrender = surrender;
        }

        public CommandWhisperToMaster getWhisperToMaster() {
            return whisperToMaster;
        }
        public void setWhisperToMaster(CommandWhisperToMaster whisperToMaster) {
            this.whisperToMaster = whisperToMaster;
        }
    }

    /** Command that happen. */
    public static class PlayerCommand {
        @JsonProperty(required = true)
        private EntityId player;
        @JsonProperty(required = true)
        private CommandHolder command;

        public EntityId getPlayer() {
            return player;
        }
        public void setPlayer(EntityId player) {
            this.player = player;
        }

        public CommandHolder getCommand() {
            return command;
        }
        public void setCommand(CommandHolder command) {
            this.command = command;
        }
    }

    public enum WhyCanNotPlayCardThere {
        DoesNotHaveEnoughPower(0x10),
        /** too close to (0,y), or (x,0) */
        InvalidPosition(0x20),
        CardCondition(0x80),
        ConditionPreventCardPlay(0x100),
        DoesNotHaveThatCard(0x200),
        DoesNotHaveEnoughOrbs(0x400),
        CastingTooOften(0x10000);

        //----------------------------------------
        public final int value;
        WhyCanNotPlayCardThere(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        public static Optional<WhyCanNotPlayCardThere> fromValue(int value) {
            return Arrays.stream(WhyCanNotPlayCardThere.values())
                    .filter(whyNot -> whyNot.value == value)
                    .findFirst();
        }
    }

    public enum CommandRejectionReasonType {
        CardRejected,
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

    public interface CommandRejectionReason extends MultiType<CommandRejectionReasonType> {
    }

    /** Rejection reason for `BuildHouse`, `ProduceSquad`, and `ProduceSquadOnBarrier` */
    public static final class CommandRejectionReasonCardRejected implements CommandRejectionReason {
        @JsonProperty(required = true)
        private WhyCanNotPlayCardThere reason;
        @JsonProperty(value = "failed_card_conditions", required = true)
        private List<Integer> failedCardConditions;

        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.CardRejected;
        }

        public WhyCanNotPlayCardThere getReason() {
            return reason;
        }
        public void setReason(WhyCanNotPlayCardThere reason) {
            this.reason = reason;
        }

        public List<Integer> getFailedCardConditions() {
            return failedCardConditions;
        }
        public void setFailedCardConditions(List<Integer> failedCardConditions) {
            this.failedCardConditions = failedCardConditions;
        }
    }

    /** Player did not have enough power to play the card or activate the ability */
    public static final class CommandRejectionReasonNotEnoughPower implements CommandRejectionReason {
        /** actual player power */
        @JsonProperty(value = "player_power", required = true)
        private float playerPower;
        /** required power to play the card */
        @JsonProperty(required = true)
        private int required;

        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.NotEnoughPower;
        }
    }

    /** Spell with given ID does not exist */
    public static final class CommandRejectionReasonSpellDoesNotExist implements CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.SpellDoesNotExist;
        }
    }

    /**
     * The entity is not on the map.
     * Possible reason is that the entity died in the meantime.
     */
    public static final class CommandRejectionReasonEntityDoesNotExist implements CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.EntityDoesNotExist;
        }
    }

    /** Entity exist, but type is not correct */
    public static final class CommandRejectionReasonInvalidEntityType implements CommandRejectionReason {
        @JsonProperty(value = "entity_type", required = true)
        private int entityType;

        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.InvalidEntityType;
        }

        public int getEntityType() {
            return entityType;
        }
        public void setEntityType(int entityType) {
            this.entityType = entityType;
        }
    }

    /** Rejection reason for `CastSpellEntity` */
    public static final class CommandRejectionReasonCanNotCast implements CommandRejectionReason {
        @JsonProperty(value = "failed_spell_conditions", required = true)
        private List<Integer> failedSpellConditions;

        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.CanNotCast;
        }

        public List<Integer> getFailedSpellConditions() {
            return failedSpellConditions;
        }
        public void setFailedSpellConditions(List<Integer> failedSpellConditions) {
            this.failedSpellConditions = failedSpellConditions;
        }
    }

    /** Bot issued command for an entity that is not owned by anyone */
    public static final class CommandRejectionReasonEntityNotOwned implements CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.EntityNotOwned;
        }
    }

    /** Bot issued command for entity owned by someone else */
    public static final class CommandRejectionReasonEntityOwnedBySomeoneElse implements CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.EntityOwnedBySomeoneElse;
        }
    }

    /** Bot issued command for entity to change mode, but the entity does not have `ModeChange` aspect. */
    public static final class CommandRejectionReasonNoModeChange implements CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.NoModeChange;
        }
    }

    /** Trying to change to mode, in which the entity already is. */
    public static final class CommandRejectionReasonEntityAlreadyInThisMode implements CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.EntityAlreadyInThisMode;
        }
    }

    /** Trying to change to mode, that the entity does not have. */
    public static final class CommandRejectionReasonModeNotExist implements CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.ModeNotExist;
        }
    }

    /** Card index must be 0-19 */
    public static final class CommandRejectionReasonInvalidCardIndex implements CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.InvalidCardIndex;
        }
    }

    /** Card on the given index is invalid */
    public static final class CommandRejectionReasonInvalidCard implements CommandRejectionReason {
        @Override
        @JsonIgnore
        public CommandRejectionReasonType getType() {
            return CommandRejectionReasonType.InvalidCard;
        }
    }

    /** Reason why command was rejected */
    public static class CommandRejectionReasonHolder {
        /** Rejection reason for `BuildHouse`, `ProduceSquad`, and `ProduceSquadOnBarrier` */
        @JsonProperty(value = "CardRejected")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonCardRejected cardRejected;
        /** Player did not have enough power to play the card or activate the ability */
        @JsonProperty(value = "NotEnoughPower")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonNotEnoughPower notEnoughPower;
        /** Spell with given ID does not exist */
        @JsonProperty(value = "SpellDoesNotExist")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonSpellDoesNotExist spellDoesNotExist;
        /** The entity is not on the map */
        @JsonProperty(value = "EntityDoesNotExist")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonEntityDoesNotExist entityDoesNotExist;
        /** Entity exist, but type is not correct */
        @JsonProperty(value = "InvalidEntityType")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonInvalidEntityType invalidEntityType;
        /** Rejection reason for `CastSpellEntity` */
        @JsonProperty(value = "CanNotCast")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonCanNotCast canNotCast;
        /** Bot issued command for an entity that is not owned by anyone */
        @JsonProperty(value = "EntityNotOwned")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonEntityNotOwned entityNotOwned;
        /** Bot issued command for entity owned by someone else */
        @JsonProperty(value = "EntityOwnedBySomeoneElse")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonEntityOwnedBySomeoneElse entityOwnedBySomeoneElse;
        /** Bot issued command for entity to change mode, but the entity does not have `ModeChange` aspect. */
        @JsonProperty(value = "NoModeChange")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonNoModeChange noModeChange;
        /** Trying to change to mode, in which the entity already is. */
        @JsonProperty(value = "EntityAlreadyInThisMode")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonEntityAlreadyInThisMode entityAlreadyInThisMode;
        /** Trying to change to moe, that the entity does not have. */
        @JsonProperty(value = "ModeNotExist")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonModeNotExist modeNotExist;
        /** Card index must be 0-19 */
        @JsonProperty(value = "InvalidCardIndex")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonInvalidCardIndex invalidCardIndex;
        /** Card on the given index is invalid */
        @JsonProperty(value = "InvalidCard")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommandRejectionReasonInvalidCard invalidCard;

        public CommandRejectionReason get() {
            if (cardRejected != null) { return cardRejected; }
            else if (notEnoughPower != null) { return notEnoughPower; }
            else if (spellDoesNotExist != null) { return spellDoesNotExist; }
            else if (entityDoesNotExist != null) { return entityDoesNotExist; }
            else if (invalidEntityType != null) { return invalidEntityType; }
            else if (canNotCast != null) { return canNotCast; }
            else if (entityNotOwned != null) { return entityNotOwned; }
            else if (entityOwnedBySomeoneElse != null) { return entityOwnedBySomeoneElse; }
            else if (noModeChange != null) { return noModeChange; }
            else if (entityAlreadyInThisMode != null) { return entityAlreadyInThisMode; }
            else if (modeNotExist != null) { return modeNotExist; }
            else if (invalidCardIndex != null) { return invalidCardIndex; }
            else if (invalidCard != null) { return invalidCard; }
            else { throw new IllegalStateException("CommandRejectionReasonHolder doesn't contain any CommandRejectionReason. Check implementation and API!"); }

        }
        public CommandRejectionReasonHolder() { }
        public CommandRejectionReasonHolder(CommandRejectionReason rejectionReason) {
            Objects.requireNonNull(rejectionReason, "CommandRejectionReason must not be null");
            switch (rejectionReason.getType()) {
                case CommandRejectionReasonType.CardRejected:
                    cardRejected = (CommandRejectionReasonCardRejected) rejectionReason;
                    break;
                case CommandRejectionReasonType.NotEnoughPower:
                    notEnoughPower = (CommandRejectionReasonNotEnoughPower) rejectionReason;
                    break;
                case CommandRejectionReasonType.SpellDoesNotExist:
                    spellDoesNotExist = (CommandRejectionReasonSpellDoesNotExist) rejectionReason;
                    break;
                case CommandRejectionReasonType.EntityDoesNotExist:
                    entityDoesNotExist = (CommandRejectionReasonEntityDoesNotExist) rejectionReason;
                    break;
                case CommandRejectionReasonType.InvalidEntityType:
                    invalidEntityType = (CommandRejectionReasonInvalidEntityType) rejectionReason;
                    break;
                case CommandRejectionReasonType.CanNotCast:
                    canNotCast = (CommandRejectionReasonCanNotCast) rejectionReason;
                    break;
                case CommandRejectionReasonType.EntityNotOwned:
                    entityNotOwned = (CommandRejectionReasonEntityNotOwned) rejectionReason;
                    break;
                case CommandRejectionReasonType.EntityOwnedBySomeoneElse:
                    entityOwnedBySomeoneElse = (CommandRejectionReasonEntityOwnedBySomeoneElse) rejectionReason;
                    break;
                case CommandRejectionReasonType.NoModeChange:
                    noModeChange = (CommandRejectionReasonNoModeChange) rejectionReason;
                    break;
                case CommandRejectionReasonType.EntityAlreadyInThisMode:
                    entityAlreadyInThisMode = (CommandRejectionReasonEntityAlreadyInThisMode) rejectionReason;
                    break;
                case CommandRejectionReasonType.ModeNotExist:
                    modeNotExist = (CommandRejectionReasonModeNotExist) rejectionReason;
                    break;
                case CommandRejectionReasonType.InvalidCardIndex:
                    invalidCardIndex = (CommandRejectionReasonInvalidCardIndex) rejectionReason;
                    break;
                case CommandRejectionReasonType.InvalidCard:
                    invalidCard = (CommandRejectionReasonInvalidCard) rejectionReason;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown CommandRejectionReason " + rejectionReason.getType());
            }
        }

        public CommandRejectionReasonCardRejected getCardRejected() {
            return cardRejected;
        }
        public void setCardRejected(CommandRejectionReasonCardRejected cardRejected) {
            this.cardRejected = cardRejected;
        }

        public CommandRejectionReasonNotEnoughPower getNotEnoughPower() {
            return notEnoughPower;
        }
        public void setNotEnoughPower(CommandRejectionReasonNotEnoughPower notEnoughPower) {
            this.notEnoughPower = notEnoughPower;
        }

        public CommandRejectionReasonSpellDoesNotExist getSpellDoesNotExist() {
            return spellDoesNotExist;
        }
        public void setSpellDoesNotExist(CommandRejectionReasonSpellDoesNotExist spellDoesNotExist) {
            this.spellDoesNotExist = spellDoesNotExist;
        }

        public CommandRejectionReasonEntityDoesNotExist getEntityDoesNotExist() {
            return entityDoesNotExist;
        }
        public void setEntityDoesNotExist(CommandRejectionReasonEntityDoesNotExist entityDoesNotExist) {
            this.entityDoesNotExist = entityDoesNotExist;
        }

        public CommandRejectionReasonInvalidEntityType getInvalidEntityType() {
            return invalidEntityType;
        }
        public void setInvalidEntityType(CommandRejectionReasonInvalidEntityType invalidEntityType) {
            this.invalidEntityType = invalidEntityType;
        }

        public CommandRejectionReasonCanNotCast getCanNotCast() {
            return canNotCast;
        }
        public void setCanNotCast(CommandRejectionReasonCanNotCast canNotCast) {
            this.canNotCast = canNotCast;
        }

        public CommandRejectionReasonEntityNotOwned getEntityNotOwned() {
            return entityNotOwned;
        }
        public void setEntityNotOwned(CommandRejectionReasonEntityNotOwned entityNotOwned) {
            this.entityNotOwned = entityNotOwned;
        }

        public CommandRejectionReasonEntityOwnedBySomeoneElse getEntityOwnedBySomeoneElse() {
            return entityOwnedBySomeoneElse;
        }
        public void setEntityOwnedBySomeoneElse(CommandRejectionReasonEntityOwnedBySomeoneElse entityOwnedBySomeoneElse) {
            this.entityOwnedBySomeoneElse = entityOwnedBySomeoneElse;
        }

        public CommandRejectionReasonNoModeChange getNoModeChange() {
            return noModeChange;
        }
        public void setNoModeChange(CommandRejectionReasonNoModeChange noModeChange) {
            this.noModeChange = noModeChange;
        }

        public CommandRejectionReasonEntityAlreadyInThisMode getEntityAlreadyInThisMode() {
            return entityAlreadyInThisMode;
        }
        public void setEntityAlreadyInThisMode(CommandRejectionReasonEntityAlreadyInThisMode entityAlreadyInThisMode) {
            this.entityAlreadyInThisMode = entityAlreadyInThisMode;
        }

        public CommandRejectionReasonModeNotExist getModeNotExist() {
            return modeNotExist;
        }
        public void setModeNotExist(CommandRejectionReasonModeNotExist modeNotExist) {
            this.modeNotExist = modeNotExist;
        }

        public CommandRejectionReasonInvalidCardIndex getInvalidCardIndex() {
            return invalidCardIndex;
        }
        public void setInvalidCardIndex(CommandRejectionReasonInvalidCardIndex invalidCardIndex) {
            this.invalidCardIndex = invalidCardIndex;
        }

        public CommandRejectionReasonInvalidCard getInvalidCard() {
            return invalidCard;
        }
        public void setInvalidCard(CommandRejectionReasonInvalidCard invalidCard) {
            this.invalidCard = invalidCard;
        }
    }

    /** Command that was rejected. */
    public static class RejectedCommand {
        @JsonProperty(required = true)
        private EntityId player;
        @JsonProperty(required = true)
        private CommandRejectionReasonHolder reason;
        @JsonProperty(required = true)
        private CommandHolder command;

        public EntityId getPlayer() {
            return player;
        }
        public void setPlayer(EntityId player) {
            this.player = player;
        }

        public CommandRejectionReasonHolder getReason() {
            return reason;
        }
        public void setReason(CommandRejectionReasonHolder reason) {
            this.reason = reason;
        }

        public CommandHolder getCommand() {
            return command;
        }
        public void setCommand(CommandHolder command) {
            this.command = command;
        }
    }

    /** Response on the `/hello` endpoint. */
    public static class AiForMap {
        /** The unique name of the bot. */
        @JsonProperty(required = true)
        private String name;
        /**
         * List of decks this bot can use on the map.
         * Empty to signalize, that bot can not play on given map.
         */
        @JsonProperty(required = true)
        private Set<Deck> decks;

        public AiForMap() {}
        /**
         * Creates a new AiForMap as response for a "/hello" request.
         * @param name The unique name of the bot
         * @param decks List of decks this bot can use on the map.
         *              Empty to signalize, that bot can not play on given map.
         */
        public AiForMap(String name, Set<Deck> decks) {
            this.name = name;
            this.decks = decks;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public Set<Deck> getDecks() {
            return decks;
        }
        public void setDecks(Set<Deck> decks) {
            this.decks = decks;
        }
    }

    /** Used in `/start` endpoint. */
    public static class GameStartState {
        /**
         * Tells the bot which player it is supposed to control.
         * If bot is only spectating, this is the ID of player that it is spectating for.
         */
        @JsonProperty(value = "your_player_id", required = true)
        private EntityId yourPlayerId;
        /** Players in the match. */
        @JsonProperty(required = true)
        private List<MatchPlayer> players;
        @JsonProperty(required = true)
        private MapEntities entities;

        public EntityId getYourPlayerId() {
            return yourPlayerId;
        }
        public void setYourPlayerId(EntityId yourPlayerId) {
            this.yourPlayerId = yourPlayerId;
        }

        public List<MatchPlayer> getPlayers() {
            return players;
        }
        public void setPlayers(List<MatchPlayer> players) {
            this.players = players;
        }

        public MapEntities getEntities() {
            return entities;
        }
        public void setEntities(MapEntities entities) {
            this.entities = entities;
        }
    }

    /** Used in `/tick` endpoint, on every tick from 2 forward. */
    public static class GameState {
        /**
         * Time since start of the match measured in ticks.
         * One tick is 0.1 second = 100 milliseconds = (10 ticks per second)
         * Each tick is 100 ms. 1 second is 10 ticks. 1 minute is 600 ticks.
         */
        @JsonProperty(value = "current_tick", required = true)
        private Tick currentTick;
        /** Commands that will be executed this tick. */
        @JsonProperty(value = "commands", required = true)
        private List<PlayerCommand> commands;
        /** Commands that were rejected. */
        @JsonProperty(value = "rejected_commands", required = true)
        private List<RejectedCommand> rejectedCommands;
        /** player entities in the match */
        @JsonProperty(value = "players", required = true)
        private List<PlayerEntity> players;
        @JsonProperty(value = "entities", required = true)
        private MapEntities entities;

        public Tick getCurrentTick() {
            return currentTick;
        }
        public void setCurrentTick(Tick currentTick) {
            this.currentTick = currentTick;
        }

        public List<PlayerCommand> getCommands() {
            return commands;
        }
        public void setCommands(List<PlayerCommand> commands) {
            this.commands = commands;
        }

        public List<RejectedCommand> getRejectedCommands() {
            return rejectedCommands;
        }
        public void setRejectedCommands(List<RejectedCommand> rejectedCommands) {
            this.rejectedCommands = rejectedCommands;
        }

        public List<PlayerEntity> getPlayers() {
            return players;
        }
        public void setPlayers(List<PlayerEntity> players) {
            this.players = players;
        }

        public MapEntities getEntities() {
            return entities;
        }
        public void setEntities(MapEntities entities) {
            this.entities = entities;
        }
    }

    /** Used in `/prepare` endpoint */
    public static class Prepare {
        /** Name of deck, selected from `AiForMap` returned by `/hello` endpoint. */
        @JsonProperty(value = "deck", required = true)
        private String deck;
        /** Repeating `map_info` in case bot want to prepare differently based on map. */
        @JsonProperty(value = "map_info", required = true)
        private MapInfo mapInfo;

        public String getDeck() {
            return deck;
        }
        public void setDeck(String deck) {
            this.deck = deck;
        }

        public MapInfo getMapInfo() {
            return mapInfo;
        }
        public void setMapInfo(MapInfo mapInfo) {
            this.mapInfo = mapInfo;
        }
    }

    /** Used in `/hello` endpoint */
    public static class ApiHello {
        /** Must match the {@link ApiVersion#VERSION version of the used API}, to guarantee structures matching. */
        @JsonProperty(required = true)
        private long version;
        /** Myp about which is the game asking. */
        @JsonProperty(value = "map", required = true)
        private MapInfo mapInfo;

        public long getVersion() {
            return version;
        }
        public void setVersion(long version) {
            this.version = version;
        }

        public MapInfo getMapInfo() {
            return mapInfo;
        }
        public void setMapInfo(MapInfo mapInfo) {
            this.mapInfo = mapInfo;
        }
    }
}
