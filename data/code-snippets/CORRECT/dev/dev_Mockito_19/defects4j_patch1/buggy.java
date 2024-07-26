public class test {
    private boolean injectMockCandidatesOnFields(Set<Object> mocks, Object instance, boolean injectionOccurred, List<Field> orderedInstanceFields) {
        for (Iterator<Field> it = orderedInstanceFields.iterator(); it.hasNext(); ) {
            Field field = it.next();
            Object injected = mockCandidateFilter.filterCandidate(mocks, field, instance).thenInject();
            if (injected != null) {
                injectionOccurred |= true;
                mocks.remove(injected);
                it.remove();
            }
        }
        return injectionOccurred;
    }
    public OngoingInjecter filterCandidate(final Collection<Object> mocks, final Field field, final Object fieldInstance) {
        if(mocks.size() == 1) {
            final Object matchingMock = mocks.iterator().next();

            return new OngoingInjecter() {
                public Object thenInject() {
                    try {
                        if (!new BeanPropertySetter(fieldInstance, field).set(matchingMock)) {
                            new FieldSetter(fieldInstance, field).set(matchingMock);
                        }
                    } catch (RuntimeException e) {
                        new Reporter().cannotInjectDependency(field, matchingMock, e);
                    }
                    return matchingMock;
                }
            };
        }

        return new OngoingInjecter() {
            public Object thenInject() {
                return null;
            }
        };

    }
	public OngoingInjecter filterCandidate(Collection<Object> mocks,
			Field field, Object fieldInstance) {
		List<Object> mockNameMatches = new ArrayList<Object>();
		if (mocks.size() > 1) {
			for (Object mock : mocks) {
				if (field.getName().equals(mockUtil.getMockName(mock).toString())) {
					mockNameMatches.add(mock);
				}
			}
			return next.filterCandidate(mockNameMatches, field,
					fieldInstance);
			/*
			 * In this case we have to check whether we have conflicting naming
			 * fields. E.g. 2 fields of the same type, but we have to make sure
			 * we match on the correct name.
			 * 
			 * Therefore we have to go through all other fields and make sure
			 * whenever we find a field that does match its name with the mock
			 * name, we should take that field instead.
			 */
		}
		return next.filterCandidate(mocks, field, fieldInstance);
	}
    public OngoingInjecter filterCandidate(Collection<Object> mocks, Field field, Object fieldInstance) {
        List<Object> mockTypeMatches = new ArrayList<Object>();
        for (Object mock : mocks) {
            if (field.getType().isAssignableFrom(mock.getClass())) {
                mockTypeMatches.add(mock);
            }
        }

        return next.filterCandidate(mockTypeMatches, field, fieldInstance);
    }
}