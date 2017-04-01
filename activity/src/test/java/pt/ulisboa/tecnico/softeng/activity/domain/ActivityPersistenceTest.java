package pt.ulisboa.tecnico.softeng.activity.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class ActivityPersistenceTest {

	private static final String ACTIVITY_NAME = "Activity_Name";
	private static final String PROVIDER_NAME = "Wicket";
	private static final String PROVIDER_CODE = "A12345";

	@Test
	public void success() {
		atomicProcess();
		atomicAssert();
	}

	@Atomic(mode = TxMode.WRITE)
	public void atomicProcess() {
		ActivityProvider activityProvider = new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME);

		new Activity(activityProvider, ACTIVITY_NAME, 18, 65, 25);
	}

	@Atomic(mode = TxMode.READ)
	public void atomicAssert() {
		assertEquals(1, FenixFramework.getDomainRoot().getActivityProviderSet().size());

		List<ActivityProvider> providers = new ArrayList<>(FenixFramework.getDomainRoot().getActivityProviderSet());
		ActivityProvider provider = providers.get(0);

		assertEquals(PROVIDER_CODE, provider.getCode());
		assertEquals(PROVIDER_NAME, provider.getName());
		assertEquals(1, provider.getActivitySet().size());

		List<Activity> activities = new ArrayList<>(provider.getActivitySet());
		Activity activity = activities.get(0);

		assertEquals(ACTIVITY_NAME, activity.getName());
		assertTrue(activity.getCode().startsWith(PROVIDER_CODE));
		assertEquals(18, activity.getMinAge());
		assertEquals(65, activity.getMaxAge());
		assertEquals(25, activity.getCapacity());
	}

	@After
	@Atomic(mode = TxMode.WRITE)
	public void tearDown() {
		for (ActivityProvider activityProvider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			activityProvider.delete();
		}
	}

}
