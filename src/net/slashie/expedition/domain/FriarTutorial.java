package net.slashie.expedition.domain;

import net.ck.expedition.model.test.ExpeditionProperties;

public class FriarTutorial
{
	public static boolean active;
	public static int page;
	public static int maxPages;
	public static int tutorialMoment;

	public static final int AFTER_TALK_KING = 0;
	public static final int ENTERING_TOWN = 1;
	public static final int LEAVING_TOWN = 2;
	public static final int APROACHING_LAND = 3;
	public static final int FIRST_LAND = 4;

	public static String[] getTextArray(int moment)
	{
		if (moment == AFTER_TALK_KING)
		{
			String[] ret =
			{ "Go south (press down key) to abandon the castle and go to town." };
			return ret;
		}
		else if (moment == ENTERING_TOWN)
		{
			String[] ret =
			{ "Enter the houses with signs in order to buy things and hire people for your expedition.",
					"You will need at least supplies for 30 days in order to cross the sea." };
			return ret;
		}
		else if (moment == LEAVING_TOWN)
		{
			String[] ret =
			{ "Listen to me carefully if you want to survive.",
					"Use the left and right arrow keys to rotate your ships. Use up to sail forward.",
					"Try to follow the wind (circle indicator) for better performance. Avoid Storms and reefs.",
					"As time goes by, your expedition will consume food. Keep and eye on the food days indicator",
					"Sail southwest to the Canary Islands and then west to get the best winds" };
			return ret;
		}
		else if (moment == APROACHING_LAND)
		{
			String[] ret =
			{ "You are approaching land, put your ships near to the shore and drop anchors ",
					"to prevent crashing, then press \"D\" to create a land expedition. Be sure to supply ",
					"your expedition with enough food, while leaving food for the people on ships" };
			return ret;
		}
		else if (moment == FIRST_LAND)
		{
			String[] ret =
			{ "Look for a nice place for your first town, you can use it as your base ",
					"of operations for your exploratory travels" };
			return ret;
		}

		return null;
	}

	public static String getText()
	{
		return getTextArray(tutorialMoment)[page];
	}

	public static void activate(int moment)
	{
		if (ExpeditionProperties.isDebug())
		{

		}
		else
		{
			tutorialMoment = moment;
			page = 0;
			active = true;
			maxPages = getTextArray(moment).length;
		}
	}

	public static void deactivate()
	{
		active = false;
		page = 0;
	}

	public static boolean nextPage()
	{
		page += 1;
		if (page >= maxPages)
		{
			deactivate();
			return false;
		}

		return true;
	}
}
