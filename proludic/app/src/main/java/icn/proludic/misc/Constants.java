package icn.proludic.misc;

import icn.proludic.R;

/**
 * Author:  Bradley Wilson
 * Date: 04/04/2017
 * Package: icn.proludic.misc
 * Project Name: proludic
 */

public class Constants {

    //init sashido
    public final static String SASHIDO_APP_ID = "v7nlBfEXBTOFeS4nVDXk3ZQYi8hiITeEMEGJPz8k";
    public final static String SASHIDO_CLIENT_ID = "NuzWJ7b4UheIoOufV7m7wgZVNJVPTiQzI8i8IOEE";
    public final static String SASHIDO_SERVER_ID = "https://pg-app-lg48xzrdk8qyfv1d6zi0ebeld3i9ku.scalabl.cloud/1/";
    public final static String SASHIDO_APP_ID_FR = "QfUWGabwsrEYMKD9RfLyysGqRQDPPDdNMnkY7Pce";
    public final static String SASHIDO_CLIENT_ID_FR = "ToY7GCSah28TAp5WbJofkgCYcQSNYPIdngDWk7PP";
    public final static String SASHIDO_SERVER_ID_FR = "https://pg-app-twku3qkkp8p6cpihk2y7yiyurlkcdc.scalabl.cloud/1/";
    public final static String GCM_SENDER_ID = "1019532414928";

    //time measurements
    public final static int ONE_SECOND = 1000;
    public final static int ONE_SEVEN_FIVE_SECONDS = 1750;
    public final static int TWO_SECONDS = 2000;
    public final static int TWO_FIVE_SECONDS = 2500;
    public final static int THREE_SECONDS = 3000;
    public final static int FIVE_SECONDS = 5000;

    //Splash screen
    public final static String INTRODUCTION_VIDEO = "Introduction_Video";
    public final static String STANDARD_RUN = "Upgrade_or_Normal";
    public final static String RUN_TYPE = "runType";

    //login page
    public final static int LOGIN_VA_CHILD_ONE = 0;
    public final static int LOGIN_VA_CHILD_TWO = 1;
    public final static int LOGIN_VA_CHILD_THREE = 2;
    public final static float HEIGHT_ADJUSTMENT = 1.5f;
    final static int MARGIN_20 = 22;
    final static int MAX_LINES = 1;
    public final static String LOGIN_TYPE = "login_type";
    public final static String NAME = "Name";
    public final static String USERNAME = "Username";
    public final static String AGE = "Age";
    public final static String USERNAME_INPUT = "Username_Input";
    public final static String BROWSE_FRAGMENT_TAG = "Browse_Fragment";
    public final static String ADD_WORKOUT_FRAGMENT_TAG = "Add_Workout_Fragment";
    public final static String EMAIL = "Email";
    public final static String FACEBOOK = "Facebook";
    public final static String ADMIN = "Admin";
    public final static String PASSWORD = "Password";
    public final static String FIRST_RUN = "FirstRun";
    public final static String NORMAL_RUN = "NormalRun";
    public final static String UPGRADED_RUN = "UpgradedRun";
    public final static String NO_PICTURE = "NoPicture";
    public final static String USER_WEIGHT = "TotalWeight";
    public final static String USER_EXERCISES = "TotalExercises";
    public final static String USER_ACHIEVEMENTS = "TotalAchievements";
    public final static String USER_DESCRIPTION = "Description";
    public final static String USER_FRIENDS = "Friends";
    public final static String USER_IS_OVER_18 = "isOver18";
    public final static String USER_IS_MALE = "isMale";
    public final static String USER_BODY_WEIGHT = "bodyWeight";
    public final static String USER_HEIGHT = "height";
    public final static String USER = "User";
    public final static String USER_FACEBOOK_ID = "facebookID";
    public final static String NOT_SELECTED = "NotSelected";
    public final static String EMPTY = "";
    public final static int LENGTH_SHORT = 0;
    public final static int LENGTH_LONG = 1;
    public final static String FB_PUBLIC_PROFILE = "public_profile";
    public final static String FB_EMAIL = "email";
    public final static String FB_USER_BIRTHDAY = "user_birthday";
    public final static String FB_USER_FRIENDS = "user_friends";
    public final static String FB_USER_EVENTS = "user_events";
    public final static int LOGOUT_ALERT_ID = 10001;
    final static String RESET_PASSWORD = "ResetPassword";

    //dashboard
    public final static int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1001;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public final static double TWENTY_FIVE_METERS = 0.025;
    public final static double TWENTY_FIVE_MILES = 40.2336;
    public final static double ONE_HUNDRED_MILES = 160.934;
    public final static int CURRENT_PARK = 2010;
    public final static String STANDARD_TOOLBAR = "Standard_Toolbar";
    public final static String SEARCH_TOOLBAR = "Search_Toolbar";

    //exercises && workouts
    public final static String EXERCISES_KEY  = "exercises_key";
    public final static String WORKOUTS_KEY  = "workouts_key";
    public final static String BROWSE_ALL = "browse_all";
    public final static String SINGLE_FITNESS_TAG = "single_fitness_key";
    public final static String FITNESS_NAME_TAG = "fitness_name_tag";
    public final static String MOST_USED = "most_used";
    public final static String FAVOURITED = "Favourited";
    public final static String MY_EXERCISES = "my_exercises";
    public final static String MY_WORKOUTS = "my_workouts";
    public final static String USER_HEARTS = "Hearts";
    public final static String FAVOURITE_EXERCISES = "FavouriteExercises";
    public final static String USER_ACHIEVEMENTS_CLASS = "UserAchievements";
    public final static String FAVOURITE_WORKOUTS = "FavouriteWorkouts";
    public final static String ALREADY_HAS_DATA = "alreadyHasData";
    public final static String EXERCISE_NAME_KEY = "ExerciseName";
    public final static String EXERCISE_IMAGE_KEY = "ExerciseImage";
    public final static String EXERCISE_MUSCLE_GROUP_IMAGE_KEY = "MuscleGroupImage";
    public final static String EXERCISE_WEIGHT  = "Weight";
    public final static String EXERCISE_REST_TIME = "ExerciseRestTime";
    public final static String EXERCISE_VIDEO_URL_KEY = "VideoURL";
    public final static String EXERCISE_DESCRIPTION_KEY = "Description";
    public final static String EXERCISE_TOTAL_WEIGHT = "TotalWeight";
    public final static String EXERCISE_TOTAL_REPS = "reps";
    public final static String EXERCISE_RESISTANCE = "Resistance";
    public final static String EXERCISE_TOTAL_SETS = "sets";
    public final static String LOCATION_STARTING_POINT_KEY = "StartingPoint";
    public final static String WORKOUT_NAME_KEY = "WorkoutName";
    public final static String EXERCISE_IDS = "ExerciseIds";
    public final static String WORKOUT_IDS = "WorkoutIds";
    public final static String EXERCISES_CLASS_NAME = "Exercises";
    public final static String PRESET_WORKOUT_CLASS_NAME = "PresetWorkouts";
    public final static String WORKOUT_DESCRIPTION_KEY = "WorkoutDescription";
    public final static String WORKOUT_BRAND_IMAGE_KEY = "BrandImage";
    public final static String WORKOUT_BRAND_NAME_KEY = "BrandName";
    public final static String HOME_PARK_KEY = "HomePark";
    public final static String AVERAGE_REST_TIME = "AverageRestTime";
    public final static String EXERCISE_LIST = "ec";
    public final static String TOTAL_EXERCISES = "TotalExercises";
    public final static String TOTAL_WEIGHT_EXERCISES = "TotalWeightExercises";
    public final static String TOTAL_NON_WEIGHT_EXERCISES = "TotalNonWeightExercises";
    public final static String WORKOUT_AVERAGE_TIME = "AverageWorkoutTime";
    public final static int HOME_PARK_DIALOG = 1337;
    public final static int SAVE_DESCRIPTION = 2017;
    public final static String LOCATION_NAME_KEY = "Location";
    public final static String LOCATION_IMAGE_KEY = "Image";
    public final static String LOCATION_TOTAL_HEARTS = "TotalParkHearts";
    public final static String LOCATIONS_CLASS_KEY = "Locations";
    public final static String EXERCISE_OF_THE_WEEK = "ExerciseWeekly";
    public final static String EXTRAS_CLASS_NAME = "Extras";
    public final static String HOME_PARK_AND_CURRENT_TYPE = "homeParkCurrentParkType";
    public final static String NORMAL_PARK_TYPE = "normalParkType";
    public final static String NO_PARK_TYPE = "noParkType";
    public final static String SINGLE_HOME_PARK_TYPE = "singleHomePark";
    public final static String EXERCISE_AVERAGE_TIME = "AverageTime";
    public final static String EXERCISE_AVERAGE_REPS = "AverageReps";
    public final static int LOCATION_SERVICES = 420;
    public final static int THREE_HUNDRED_HEARTS = 300;
    public final static String FRIEND_REQUESTS_CLASS_NAME = "FriendRequests";
    public final static String FRIEND_REQUESTS_USER_REQUESTED = "UserRequested";
    public final static String FRIEND_REQUESTS_USER_REQUESTING = "UserRequesting";
    public final static String FRIEND_REQUESTS_IS_CHALLENGE = "isChallenge";
    public final static String FRIEND_REQUESTS_IS_COMPLETE = "isComplete";
    public final static String FRIEND_REQUESTS_IS_PENDING = "isPending";
    public final static String FRIEND_REQUESTS_ACCEPTED = "accepted";
    public final static String FRIEND_REQUESTS_WEIGHT = "isWeight";
    public final static String EXERCISE_IS_WEIGHT = "isWeight";
    public final static String EXERCISE_NO_WEIGHT_DESCRIPTION = "noWeightDesc";
    public final static String DATE = "Date";
    public final static String USER_WINS = "wins";
    public final static String USER_DRAW = "draw";
    public final static String USER_LOSSES = "loss";
    public final static String FRIEND_REQUESTS_LENGTH = "LengthTime";
    public final static String USER_PROFILE_PICTURE = "profilePicture";
    public final static String USER_FULL_NAME = "name";
    public final static String TRACKED_EVENTS_CLASS_NAME = "TrackedEvents";
    public final static String LAST_ACTIVE = "LastActive";
    public final static String NO_DATE = "no_date";
    public final static String TRACKED_HEARTS = "Hearts";
    public final static String TRACKED_TOTAL_EXERCISES = "Exercises";
    public final static String TRACKED_USER = "User";
    public final static String TRACKED_CREATED_AT = "createdAt";
    public final static String TRACKED_EXERCISES_USED = "ExercisesUsed";
    public final static String COMMUNITY_POSTS_CLASS_NAME = "Posts";
    public final static String COMMUNITY_POSTS_OP = "OriginalPoster";
    public final static String COMMUNITY_POSTS_COMMUNITY = "Community";
    public final static String COMMUNITY_POSTS_CONTENT = "PostContent";
    public final static String COMMUNITY_POSTS_TITLE = "PostTitle";
    public final static String COMMUNITY_POSTS_REPLIES = "Replies";
    public final static String COMMUNITY_REPLIES_CLASS_NAME = "PostReplies";
    public final static String COMMUNITY_REPLY_REPLIES_CLASS_NAME = "PostReplyReplies";
    public final static String COMMUNITY_REPLIES_POST = "Post";
    public final static String COMMUNITY_REPLIES_REPLY_CONTENT = "ReplyContent";
    public final static String COMMUNITY_REPLIES_REPLYING_USER = "ReplyingUser";
    public final static String COMMUNITY_REPLIES_UPDATED_AT = "updatedAt";
    public final static String POST_KEY = "Post_Key";
    public final static String REPORTED_POSTS_CLASS_NAME = "ReportedPosts";
    public final static String REPORTED_POSTS_USER = "ReportedUser";
    public final static String REPORTED_POSTS_MODERATED = "Moderated";
    public final static String REPORTED_POSTS_REASON = "Reason";
    public final static String REPORTED_POSTS_CONTENT = "Content";

    //Shared Prefs
    final static String SHARED_PREFS = "Proludic_Shared_Prefs";
    public final static String IS_AT_PARK_KEY = "Is_At_Park";
    public final static String LONGITUDE_KEY = "LongitudeKey";
    public final static String LATITUDE_KEY = "LatitudeKey";

    //User Achievements
    public final static String USER_ACHIEVEMENTS_CLASS_NAME = "UserAchievements";
    public final static int KODIAK_BEAR_WEIGHT = 800;
    public final static int CROCODILE_WEIGHT = 1000;
    public final static int ASIAN_GUAR_WEIGHT = 1100;
    public final static int GIRAFFE_WEIGHT = 1600;
    public final static int HIPPOPOTAMUS_WEIGHT = 3400;
    public final static int WHITE_RHINOCEROS_WEIGHT = 3500;
    public final static int ASIAN_ELEPHANT_WEIGHT = 5000;
    public final static int AFRICAN_ELEPHANT_WEIGHT = 6400;
    public final static int WHALE_SHARK_WEIGHT = 18000;
    public final static int LONDON_EYE_WEIGHT = 2100000;
    public final static int EIFFEL_TOWER_WEIGHT = 7300000;
    public final static int EMPIRE_STATE_BUILDING_WEIGHT = 331000000;
    public final static int BURJ_KHALIFA_WEIGHT = 500000000;
    public final static int GOLDEN_GATE_BRIDGE_WEIGHT = 811500000;

    public final static String ACHIEVEMENTS_CLASS = "Achievements";

    /*public final static String KODIAK_BEAR_NAME = .getString(R.string.kodiak_bear);
    public final static String CROCODILE_NAME = .getString(R.string.crocodile);
    public final static String ASIAN_GUAR_NAME = .getString(R.string.asian_guar);
    public final static String GIRAFFE_NAME = .getString(R.string.giraffe);
    public final static String HIPPOPOTAMUS_NAME = .getString(R.string.hippopotamus);
    public final static String WHITE_RHINOCEROS_NAME = .getString(R.string.white_rhinoceros);
    public final static String ASIAN_ELEPHANT_NAME = .getString(R.string.asian_elephant);
    public final static String AFRICAN_ELEPHANT_NAME = .getString(R.string.african_elephant);
    public final static String WHALE_SHARK_NAME = .getString(R.string.whale_shark);
    public final static String LONDON_EYE_NAME = .getString(R.string.london_eye);
    public final static String EIFFEL_TOWER_NAME = .getString(R.string.eiffel_tower);
    public final static String EMPIRE_STATE_BUILDING_NAME = .getString(R.string.empire_state_building);
    public final static String BURJ_KHALIFA_NAME = .getString(R.string.burj_khalifa);
    public final static String GOLDEN_GATE_BRIDGE_NAME = .getString(R.string.golden_gate_bridge);
    public final static String GETTING_STARTED_NAME = .getString(R.string.getting_started);
    public final static String WORKING_OUT_NAME = .getString(R.string.working_out);
    public final static String PROFILE_PERFECT_NAME = .getString(R.string.profile_perfect);
    public final static String SOCIALIZE_NAME = .getString(R.string.socialize);
    public final static String SOCIAL_BUZZ_NAME = .getString(R.string.social_buzz);
    public final static String CHALLENGE_BEGIN_NAME = .getString(R.string.challenge_begin);
    public final static String VICTORIOUS_NAME = .getString(R.string.victorious);
    public final static String WEEKLY_WORKER_NAME = .getString(R.string.weekly_worker);
    public final static String REGISTRATION_NAME = .getString(R.string.registration);
    public final static String KEEPING_THE_PEACE_NAME = .getString(R.string.keeping_the_peace);
    public final static String PROLUDIC_COPPER_NAME = .getString(R.string.proludic_copper);
    public final static String PROLUDIC_BRONZE_NAME = .getString(R.string.proludic_bronze);
    public final static String PROLUDIC_SILVER_NAME = .getString(R.string.proludic_silver);
    public final static String PROLUDIC_GOLD_NAME = .getString(R.string.proludic_gold);
    public final static String PROLUDIC_PLATINUM_NAME = .getString(R.string.proludic_platinum);
    public final static String PROLUDIC_DIAMOND_NAME = .getString(R.string.proludic_diamond);
    public final static String BODYWEIGHT_COPPER_NAME = .getString(R.string.bodyweight_copper);
    public final static String BODYWEIGHT_BRONZE_NAME = .getString(R.string.bodyweight_bronze);
    public final static String BODYWEIGHT_SILVER_NAME = .getString(R.string.bodyweight_silver);
    public final static String BODYWEIGHT_GOLD_NAME = .getString(R.string.bodyweight_gold);
    public final static String BODYWEIGHT_PLATINUM_NAME = .getString(R.string.bodyweight_platinum);
    public final static String BODYWEIGHT_DIAMOND_NAME = .getString(R.string.bodyweight_diamond);*/

    public final static String KODIAK_BEAR_COL = "KodiakBear";
    public final static String CROCODILE_COL = "Crocodile";
    public final static String ASIAN_GUAR_COL = "AsianGuar";
    public final static String GIRAFFE_COL = "Giraffe";
    public final static String HIPPOPOTAMUS_COL = "Hippopotamus";
    public final static String WHITE_RHINOCEROS_COL= "WhiteRhinoceros";
    public final static String ASIAN_ELEPHANT_COL = "AsianElephant";
    public final static String AFRICAN_ELEPHANT_COL = "AfricanElephant";
    public final static String WHALE_SHARK_COL= "WhaleShark";
    public final static String LONDON_EYE_COL = "LondonEye";
    public final static String EIFFEL_TOWER_COL = "EiffelTower";
    public final static String EMPIRE_STATE_BUILDING_COL = "EmpireStateBuilding";
    public final static String BURJ_KHALIFA_COL = "BurjKhalifa";
    public final static String GOLDEN_GATE_BRIDGE_COL = "GoldenGateBridge";
    public final static String GETTING_STARTED_COL = "GettingStarted";
    public final static String WORKING_OUT_COL = "WorkingOut";
    public final static String PROFILE_PERFECT_COL = "ProfilePerfect";
    public final static String REGISTRATION_COL = "Registration";
    public final static String SOCIALIZE_COL = "Socialize";
    public final static String SOCIAL_BUZZ_COL = "SocialBuzz";
    public final static String CHALLENGE_BEGIN_COL = "ChallengeBegin";
    public final static String VICTORIOUS_COL = "Victorious";
    public final static String WEEKLY_WORKER_COL = "WeeklyWorker";
    public final static String KEEPING_THE_PEACE_COL = "KeepingThePeace";
    public final static String PROLUDIC_COPPER_COL = "ProludicCopper";
    public final static String PROLUDIC_BRONZE_COL = "ProludicBronze";
    public final static String PROLUDIC_SILVER_COL = "ProludicSilver";
    public final static String PROLUDIC_GOLD_COL = "ProludicGold";
    public final static String PROLUDIC_PLATINUM_COL = "ProludicPlatinum";
    public final static String PROLUDIC_DIAMOND_COL = "ProludicDiamond";
    public final static String BODYWEIGHT_COPPER_COL = "BodyweightCopper";
    public final static String BODYWEIGHT_BRONZE_COL = "BodyweightBronze";
    public final static String BODYWEIGHT_SILVER_COL = "BodyweightSilver";
    public final static String BODYWEIGHT_GOLD_COL = "BodyweightGold";
    public final static String BODYWEIGHT_PLATINUM_COL = "BodyweightPlatinum";
    public final static String BODYWEIGHT_DIAMOND_COL = "BodyweightDiamond";

    public final static String LOCATION_LATITUDE = "LocLat";
    public final static String LOCATION_LONGITUDE = "LocLong";
    public final static String TODAY = "Today";
    public final static String WEEKLY = "Weekly";
    public final static String MONTHLY = "Monthly";
    public final static String ALL_TIME = "All Time";

    /*public final static String COPPER_RANK_NAME = .getString(R.string.copper);
    public final static String BRONZE_RANK_NAME = .getString(R.string.bronze);
    public final static String SILVER_RANK_NAME = .getString(R.string.silver);
    public final static String GOLD_RANK_NAME = .getString(R.string.gold);
    public final static String PLATINUM_RANK_NAME = .getString(R.string.platinum);
    public final static String DIAMOND_RANK_NAME = .getString(R.string.diamond);*/

    //Community
    public final static String HEARTS_LOCAL = "HeartsLocal";
    public final static String HEARTS_NATIONAL = "HeartsNational";
    public final static String ACHIEVEMENTS_LOCAL = "AchieveLocal";
    public final static String ACHIEVEMENTS_NATIONAL = "AchieveNational";
    public final static String FORUM_FRAGMENT = "ForumFragment";


    public final static String NO_VIDEO = "NO_VIDEO";
    public final static String SHARED_PREFS_OVER_18 = "isOver18";
    public final static String SHARED_PREFS_GENDER = "GENDER";
    public final static String SHARED_PREFS_HEIGHT = "HEIGHT";
    public final static String SHARED_PREFS_WEIGHT = "WEIGHT";
    public final static String SHARED_PREFS_USERNAME_OR_UPLOAD = "SHARED_PREFS_USERNAME_OR_UPLOAD";
    public final static String SHARED_PREFS_NULLS = "nulls";
    public final static String SHARED_PREFS_TERMS_ACCEPTED = "terms_accepted";
    public final static String TRUE = "TRUE";
    public final static String FALSE = "FALSE";
    public final static String MALE = "MALE";
    public final static String FEMALE = "FEMALE";
    public final static String WARNING_DISABLED = "warning_disabled";
    public final static String SETTINGS_ENABLE_3G_4G = "SETTINGS_ENABLE_3G_4G";

    public final static String TERMS_FAQS_FRAGMENT_TAG = "TERMS_FAQS_FRAGMENT";
    public final static String TERMS_KEY = "TERMS_KEY";
    public final static String FAQS_KEY = "FAQS_KEY";
    public static final String PROFILE_IS_FRIEND = "profile_is_friend";
    public static final String FRIENDS_MODEL_KEY = "friends_model_key";
    public static final String FRIENDS_FRAGMENT_TAG = "friends_fragment_tag";
}
