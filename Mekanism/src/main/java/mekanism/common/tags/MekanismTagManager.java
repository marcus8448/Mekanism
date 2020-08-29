package mekanism.common.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketMekanismTags;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.tag.SetTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraftforge.registries.IForgeRegistryEntry;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MekanismTagManager implements ResourceReloadListener {

    private final List<ForgeRegistryTagCollection<?>> tagCollections = new ArrayList<>();

    public MekanismTagManager() {
        for (ManagedTagType<?> managedType : ManagedTagType.getManagedTypes()) {
            tagCollections.add(managedType.getTagCollection());
        }
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer stage, ResourceManager resourceManager, Profiler preparationsProfiler, Profiler reloadProfiler,
          Executor backgroundExecutor, Executor gameExecutor) {
        CompletableFuture<List<TagInfo<?>>> reloadResults = CompletableFuture.completedFuture(new ArrayList<>());
        for (ForgeRegistryTagCollection<?> tagCollection : tagCollections) {
            reloadResults = combine(reloadResults, tagCollection, resourceManager, backgroundExecutor);
        }
        return reloadResults.thenCompose(stage::whenPrepared).thenAcceptAsync(results -> {
            results.forEach(TagInfo::registerAndSet);
            Mekanism.packetHandler.sendToAllIfLoaded(new PacketMekanismTags(Mekanism.instance.getTagManager()));
        }, gameExecutor);
    }

    private <T extends IForgeRegistryEntry<T>> CompletableFuture<List<TagInfo<?>>> combine(CompletableFuture<List<TagInfo<?>>> reloadResults,
          ForgeRegistryTagCollection<T> tagCollection, ResourceManager resourceManager, Executor backgroundExecutor) {
        return reloadResults.thenCombine(tagCollection.prepareReload(resourceManager, backgroundExecutor), (results, result) -> {
            results.add(new TagInfo<>(tagCollection, result));
            return results;
        });
    }

    public void setCollections() {
        tagCollections.forEach(ForgeRegistryTagCollection::setCollection);
    }

    public void write(PacketByteBuf buffer) {
        tagCollections.forEach(tagCollection -> tagCollection.write(buffer));
    }

    public static MekanismTagManager read(PacketByteBuf buffer) {
        MekanismTagManager tagManager = new MekanismTagManager();
        tagManager.tagCollections.forEach(tagCollection -> tagCollection.read(buffer));
        return tagManager;
    }

    private static class TagInfo<T extends IForgeRegistryEntry<T>> {

        private final ForgeRegistryTagCollection<T> tagCollection;
        private final Map<Identifier, SetTag.Builder> results;

        private TagInfo(ForgeRegistryTagCollection<T> tagCollection, Map<Identifier, SetTag.Builder> result) {
            this.tagCollection = tagCollection;
            this.results = result;
        }

        private void registerAndSet() {
            tagCollection.applyReload(results);
            tagCollection.setCollection();
        }
    }
}