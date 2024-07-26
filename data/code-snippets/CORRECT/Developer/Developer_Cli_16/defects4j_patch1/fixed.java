public class test {
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
}