public class test {
    public void removeSubtitle(Title title) {
        this.subtitles.remove(title);
        fireChartChanged();
    }
    public void removeLegend() {
        removeSubtitle(getLegend());
    }
}