package mekanism.api.chemical;

import java.util.List;
import java.util.Map.Entry;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import net.minecraft.tag.GlobalTagAccessor;
import net.minecraft.tag.Tag;
import net.minecraft.tag.Tag.Identified;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

public class ChemicalTags<CHEMICAL extends Chemical<CHEMICAL>> {

    public static final ChemicalTags<Gas> GAS = new ChemicalTags<>();
    public static final ChemicalTags<InfuseType> INFUSE_TYPE = new ChemicalTags<>();
    public static final ChemicalTags<Pigment> PIGMENT = new ChemicalTags<>();
    public static final ChemicalTags<Slurry> SLURRY = new ChemicalTags<>();

    private final GlobalTagAccessor<CHEMICAL> collection = new GlobalTagAccessor<>();

    private ChemicalTags() {
    }

    public void setCollection(TagContainer<CHEMICAL> collectionIn) {
        collection.setContainer(collectionIn);
    }

    public TagContainer<CHEMICAL> getCollection() {
        return collection.getContainer();
    }

    public Identifier lookupTag(Tag<CHEMICAL> tag) {
        //Manual and slightly modified implementation of TagCollection#func_232975_b_ to have better reverse lookup handling
        TagContainer<CHEMICAL> collection = getCollection();
        Identifier resourceLocation = collection.getId(tag);
        if (resourceLocation == null) {
            //If we failed to get the resource location, try manually looking it up by a "matching" entry
            // as the objects are different and neither Tag nor NamedTag override equals and hashCode
            List<CHEMICAL> chemicals = tag.values();
            for (Entry<Identifier, Tag<CHEMICAL>> entry : collection.getEntries().entrySet()) {
                if (chemicals.equals(entry.getValue().values())) {
                    resourceLocation = entry.getKey();
                    break;
                }
            }
        }
        if (resourceLocation == null) {
            throw new IllegalStateException("Unrecognized tag");
        }
        return resourceLocation;
    }

    public static Identified<Gas> gasTag(Identifier resourceLocation) {
        return chemicalTag(resourceLocation, GAS);
    }

    public static Identified<InfuseType> infusionTag(Identifier resourceLocation) {
        return chemicalTag(resourceLocation, INFUSE_TYPE);
    }

    public static Identified<Pigment> pigmentTag(Identifier resourceLocation) {
        return chemicalTag(resourceLocation, PIGMENT);
    }

    public static Identified<Slurry> slurryTag(Identifier resourceLocation) {
        return chemicalTag(resourceLocation, SLURRY);
    }

    public static <CHEMICAL extends Chemical<CHEMICAL>> Identified<CHEMICAL> chemicalTag(Identifier resourceLocation, ChemicalTags<CHEMICAL> chemicalTags) {
        return chemicalTags.collection.get(resourceLocation.toString());
    }
}