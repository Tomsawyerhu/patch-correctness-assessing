public class test {
    public void smartNullPointerException(Location location) {
        throw new SmartNullPointerException(join(
                "You have a NullPointerException here:",
                new Location(),
                "Because this method was *not* stubbed correctly:",
                location,
                ""
                ));
    }
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if (new ObjectMethodsGuru().isToString(method)) {
                return "SmartNull returned by unstubbed " + formatMethodCall()  + " method on mock";
            }

            new Reporter().smartNullPointerException(location);
            return null;
        }
}