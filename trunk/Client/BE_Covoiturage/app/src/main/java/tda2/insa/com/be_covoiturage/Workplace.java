package tda2.insa.com.be_covoiturage;

import java.util.ArrayList;

/**
 *
 * Created by remi on 10/01/15.
 */
public class Workplace {
	private int _id;
	private String _name;
	private String _address;
	private static ArrayList<Workplace> _workplaces = new ArrayList<>();

	public Workplace(int id, String name, String address) {
		_id = id;
		_name = name;
		_address = address;

		_workplaces.add(this);
	}

	public static Workplace getWithID(int id) {
		for(Workplace wp : _workplaces) {
			if(wp.getID() == id) {
				return wp;
			}
		}

		return null;
	}

	public static ArrayList<Workplace> getWorkplaces() {
		return _workplaces;
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
