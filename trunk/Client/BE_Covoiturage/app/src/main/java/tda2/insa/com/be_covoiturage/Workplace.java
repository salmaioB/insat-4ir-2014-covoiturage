package tda2.insa.com.be_covoiturage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by remi on 10/01/15.
 */
public class Workplace {
	private int _id;
	private String _name;
	private String _address;
	private static Workplace _default;
	private static HashMap<Integer, Workplace> _workplaces = new HashMap<>();

	public Workplace(int id, String name, String address) {
		_id = id;
		_name = name;
		_address = address;

		_workplaces.put(new Integer(id), this);
	}

	public static Workplace getDefaultWorkplace() {
		if(_default == null) {
			_default = new Workplace(-1, "", "");
		}

		return _default;
	}

	public static HashMap<Integer, Workplace> getWorkplaces() {
		return _workplaces;
	}

	public boolean isDefault() {
		return _id == -1;
	}

	public int getID() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public String getAddress() {
		return _address;
	}
}
