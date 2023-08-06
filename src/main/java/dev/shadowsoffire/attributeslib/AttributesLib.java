package dev.shadowsoffire.attributeslib;

import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.attributeslib.client.AttributesLibClient;
import dev.shadowsoffire.attributeslib.compat.CuriosCompat;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import dev.shadowsoffire.attributeslib.packet.CritParticleMessage;
import dev.shadowsoffire.placebo.network.MessageHelper;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(AttributesLib.MODID)
public class AttributesLib {

    public static final String MODID = "attributeslib";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final DeferredHelper R = DeferredHelper.create(MODID);

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(MODID, MODID))
        .clientAcceptedVersions(s -> true)
        .serverAcceptedVersions(s -> true)
        .networkProtocolVersion(() -> "1.0.0")
        .simpleChannel();

    public AttributesLib() {
        MinecraftForge.EVENT_BUS.register(new AttributeEvents());
        if (FMLEnvironment.dist.isClient()) {
            MinecraftForge.EVENT_BUS.register(new AttributesLibClient());
            FMLJavaModLoadingContext.get().getModEventBus().register(AttributesLibClient.class);
        }

        MessageHelper.registerMessage(CHANNEL, 0, new CritParticleMessage.Provider());
        ALObjects.bootstrap();
    }

    public static int knowledgeMult = 4;

    @SubscribeEvent
    public void init(FMLCommonSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(ALObjects.MobEffects.KNOWLEDGE.get());
    }

    public static float localAtkStrength = 1;

    @SubscribeEvent
    public void trackCooldown(AttackEntityEvent e) {
        Player p = e.getEntity();
        localAtkStrength = p.getAttackStrengthScale(0.5F);
    }

    // TODO - Update impls to reflect new default values.
    @SubscribeEvent
    public void applyAttribs(EntityAttributeModificationEvent e) {
        e.getTypes().forEach(type -> {
            addAll(type, e::add,
                ALObjects.Attributes.DRAW_SPEED,
                ALObjects.Attributes.CRIT_CHANCE,
                ALObjects.Attributes.CRIT_DAMAGE,
                ALObjects.Attributes.COLD_DAMAGE,
                ALObjects.Attributes.FIRE_DAMAGE,
                ALObjects.Attributes.LIFE_STEAL,
                ALObjects.Attributes.CURRENT_HP_DAMAGE,
                ALObjects.Attributes.OVERHEAL,
                ALObjects.Attributes.GHOST_HEALTH,
                ALObjects.Attributes.MINING_SPEED,
                ALObjects.Attributes.ARROW_DAMAGE,
                ALObjects.Attributes.ARROW_VELOCITY,
                ALObjects.Attributes.EXPERIENCE_GAINED,
                ALObjects.Attributes.HEALING_RECEIVED,
                ALObjects.Attributes.ARMOR_PIERCE,
                ALObjects.Attributes.ARMOR_SHRED,
                ALObjects.Attributes.PROT_PIERCE,
                ALObjects.Attributes.PROT_SHRED,
                ALObjects.Attributes.DODGE_CHANCE);
        });
        // Change the base value of Step Height to reflect the real base value of a Player.
        // The alternative is a bunch of special casing in the display.
        // This is course-corrected in IForgeEntityMixin.
        e.add(EntityType.PLAYER, ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6);
    }

    @SafeVarargs
    private static void addAll(EntityType<? extends LivingEntity> type, BiConsumer<EntityType<? extends LivingEntity>, Attribute> add, RegistryObject<Attribute>... attribs) {
        for (RegistryObject<Attribute> a : attribs)
            add.accept(type, a.get());
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        AttributeSupplier playerAttribs = DefaultAttributes.getSupplier(EntityType.PLAYER);
        for (Attribute attr : ForgeRegistries.ATTRIBUTES.getValues()) {
            if (playerAttribs.hasAttribute(attr)) attr.setSyncable(true);
        }
        if (ModList.get().isLoaded("curios")) {
            e.enqueueWork(CuriosCompat::init);
        }
    }

    public static TooltipFlag getTooltipFlag() {
        if (FMLEnvironment.dist.isClient()) return ClientAccess.getTooltipFlag();
        return TooltipFlag.Default.NORMAL;
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    private static class ClientAccess {
        static TooltipFlag getTooltipFlag() {
            return Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        }
    }
}