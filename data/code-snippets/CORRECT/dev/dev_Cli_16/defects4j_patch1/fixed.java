public class test {
    void setParent(Option parent);
    Option getParent();
    public void addOption(Option option) {
        options.add(option);
        nameToOption.put(option.getPreferredName(), option);

        for (Iterator i = option.getTriggers().iterator(); i.hasNext();) {
            nameToOption.put(i.next(), option);
        }

        // ensure that all parent options are also added
        Option parent = option.getParent();
        while (parent != null && !options.contains(parent)) {
            options.add(parent);
            parent = parent.getParent();
        }
    }
    public GroupImpl(final List options,
                     final String name,
                     final String description,
                     final int minimum,
                     final int maximum) {
        super(0, false);

        this.name = name;
        this.description = description;
        this.minimum = minimum;
        this.maximum = maximum;

        // store a copy of the options to be used by the
        // help methods
        this.options = Collections.unmodifiableList(options);

        // anonymous Argument temporary storage
        final List newAnonymous = new ArrayList();

        // map (key=trigger & value=Option) temporary storage
        final SortedMap newOptionMap = new TreeMap(ReverseStringComparator.getInstance());

        // prefixes temporary storage
        final Set newPrefixes = new HashSet();

        // process the options
        for (final Iterator i = options.iterator(); i.hasNext();) {
            final Option option = (Option) i.next();
            option.setParent(this);

            if (option instanceof Argument) {
                i.remove();
                newAnonymous.add(option);
            } else {
                final Set triggers = option.getTriggers();

                for (Iterator j = triggers.iterator(); j.hasNext();) {
                    newOptionMap.put(j.next(), option);
                }

                // store the prefixes
                newPrefixes.addAll(option.getPrefixes());
            }
        }

        this.anonymous = Collections.unmodifiableList(newAnonymous);
        this.optionMap = Collections.unmodifiableSortedMap(newOptionMap);
        this.prefixes = Collections.unmodifiableSet(newPrefixes);
    }
    public Option getParent() {
        return parent;
    }
    public void setParent(Option parent) {
        this.parent = parent;
    }
}