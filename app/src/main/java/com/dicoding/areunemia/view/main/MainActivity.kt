package com.dicoding.areunemia.view.main

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.CalendarData
import com.dicoding.areunemia.data.local.pref.News
import com.dicoding.areunemia.databinding.ActivityMainBinding
import com.dicoding.areunemia.utils.navigateToOtherFeature
import com.dicoding.areunemia.utils.showLoginAlertDialog
import com.dicoding.areunemia.view.ListNewsAdapter
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.account.AccountActivity
import com.dicoding.areunemia.view.adapter.CalendarAdapter
import com.dicoding.areunemia.view.history.HistoryActivity
import com.dicoding.areunemia.view.scan.ScanActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity(), CalendarAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var newsAdapter: ListNewsAdapter
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var currentCalendar: Calendar

    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize components
        initializeComponents()

        // Observe session changes
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                // User is not logged in
                showLoginAlertDialog(this)
            } else {
                // User is logged in
                setupView()
            }
        }
    }

    private fun initializeComponents() {
        // Request necessary permissions
        if (checkAndRequestPermissions()) {
            // Permissions are granted. Initialize your components.
            setupNewsRecyclerView()
            setupCalendarRecyclerView()
            setupMonthYearPicker()
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionsNeeded = ArrayList<String>()
        val permissionsList = arrayOf(
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR
        )

        for (permission in permissionsList) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
            return false
        }

        return true
    }

    private fun setupNewsRecyclerView() {
        binding.rvBerita.layoutManager = LinearLayoutManager(this)
        newsAdapter = ListNewsAdapter(this, getLocalNewsData())
        binding.rvBerita.adapter = newsAdapter
    }

    private fun setupCalendarRecyclerView() {
        currentCalendar = Calendar.getInstance()
        binding.calendarView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        calendarAdapter = CalendarAdapter(this, getCalendarData())
        binding.calendarView.adapter = calendarAdapter
    }

    private fun setupMonthYearPicker() {
        binding.monthYearPicker.setOnClickListener { showMonthYearPicker() }
        updateMonthYearPickerText()
    }

    private fun getLocalNewsData(): List<News> {
        return listOf(
            News("Can your period cause anemia?", "Anemia can sneak up on you—especially iron deficiency anemia. It can happen slowly, over years, so that you might not notice the changes. A fatigue that you can’t shake, regardless of how much you rest. Feeling easily out of breath from activities you could always do before. Unexplained changes in your hair, nails, skin. These are just some of the symptoms of iron deficiency. Anemia is a huge public health problem globally, with 1.62 billion people (24.8% of the world population) predicted to be impacted (1).\n" +
                    "\n" +
                    "First: what is anemia?\n" +
                    "Anemia is the reduction in red blood cells or hemoglobin within the body (2). Hemoglobin is an iron-containing protein within your red blood cells. Hemoglobin binds and transports oxygen molecules to the cells of your body. With fewer red blood cells, your body (including your brain) cannot receive enough oxygen and function optimally.\n" +
                    "\n" +
                    "What causes anemia?\n" +
                    "Red blood cells are made in your bone marrow and have a lifespan of approximately 110 days, during which they circulate and deliver gases throughout the body (3). As they age, they are eventually broken down in the spleen, lymph nodes, and liver, and their parts are recycled within the body. Any disruption along the lifecycle of the red blood cell (creation, functional life span, or destruction) could cause anemia. Common causes of anemia include: blood loss, parasitic infections, nutritional deficiencies, absorption problems, and chronic disease (1,2). Anemia is a broad topic. There are many different causes and manifestations of it.\n" +
                    "\n" +
                    "For the purpose of this article, though, we’ll only be focusing on iron deficiency anemia—a particular type of anemia—and how it relates to menstrual and gynecological health.\n" +
                    "\n" +
                    "Periods and anemia\n" +
                    "People who menstruate are disproportionately affected by anemia due to the fact that they lose blood through their periods.\n" +
                    "\n" +
                    "When blood is lost every month during menstruation, the iron within those red blood cells is also lost. If monthly iron intake and absorption does not replace the iron lost during your period, you can end up with iron deficiency anemia (2).\n" +
                    "\n" +
                    "People with heavy menstrual bleeding are more susceptible to iron-deficiency anemia. A person is considered to have heavy menstrual bleeding when their menstrual period is typically over 80 ml (5). Some causes of heavy menstrual bleeding can be attributed to fibroids (abnormal growth of muscle tissue on your uterus), adenomyosis (a condition where endometrial tissue invades into the muscular wall of the uterus), polyps (abnormal growths on your cervix or the inside of your uterus) or bleeding disorders (6).\n" +
                    "\n" +
                    "Menstrual periods are not the only gynecological source of iron-deficiency anemia. During pregnancy and lactation, as with any time of increased growth and development, there is an increased need for iron (2,7). It is important for pregnant people to ensure that they have adequate iron, since low iron levels can harm both parent and child (2). During pregnancy, you need 2 to 3 times the normal amount of iron you’d need when not pregnant (8). Blood loss during childbirth can also further contribute to anemia.\n" +
                    "\n" +
                    "What are the symptoms of anemia?\n" +
                    "Anemia, particularly the iron deficient type, can have an insidious onset as it can take years to slowly develop. Some of the symptoms of iron deficiency anemia include: tiredness, weakness, shortness of breath, poor concentration, lightheadedness, cold intolerance, and heart palpitations (1,2,9).\n" +
                    "\n" +
                    "Other physical signs that your healthcare provider will look for are: paleness (particularly on your inner eyelids), hair loss, chapping at the corners of your mouth, nail changes, and poor circulation (cold fingers and toes) (1,2,9).\n" +
                    "\n" +
                    "I have iron deficiency anemia. What now?\n" +
                    "Seems obvious — just eat more iron, right? Well, not necessarily. Increasing iron intake through your diet is a great place to start. There are two types of dietary iron: heme iron and nonheme iron.\n" +
                    "\n" +
                    "Heme iron sources contain hemoglobin (remember: this is the iron containing protein within blood cells), and is only found in meat sources (10). Heme iron is more readily absorbed than nonheme iron. Organ meats (like liver) generally have the highest concentrations of heme iron (11). All meat contains heme iron, not just red meat (although it does have higher concentration of heme iron), but chicken, pork and turkey are good sources. Fish, seafood, and especially shellfish like oysters are also great sources of iron (11).\n" +
                    "\n" +
                    "Nonheme iron is available from plant sources, such as grains, beans, and some vegetables (10). Nonheme iron is not absorbed as well as heme iron sources. Populations where meat intake is low can struggle to obtain enough dietary iron. Other compounds found within nonheme iron sources (such as phytates, tannin, and calcium) can also *decrease* iron absorption — so try to limit your coffee and tea intake directly after meals (12,13). Eating iron sources with vitamin C (citrus fruits, peppers, strawberries, etc) will help make dietary iron more readily absorbed (13). Some sources of high nonheme iron include: soybeans/tofu, lentils, oats, wheat (this is often fortified), beets, kale, nuts, molasses, and beans (11).\n" +
                    "\n" +
                    "Once a person has iron deficiency anemia, unfortunately, dietary iron intake isn’t always enough to correct the deficiency. Oral iron tablets and supplements are the primary treatment for iron deficiency anemia. Before taking any supplement for anemia, talk to your healthcare practitioner. There are many different types of anemia with different causes, so it is important to make sure that you are treating the appropriate type of anemia and its underlying cause.\n", R.drawable.img01),
            News("Anemia: Causes, Symptoms And Treatment", "Anemia is a disorder that results in reduced red blood cell count or lower hemoglobin levels in the blood. It is caused due to decreased hemoglobin secretion or excess loss of hemoglobin or increases damage to red blood cells.\n" +
                    "\n" +
                    "Anemia is diagnosed based on the level of hemoglobin, in men if the hemoglobin level falls below 13g/dl and women the hemoglobin of less than 12g/dl.\n" +
                    "\n" +
                    "Red blood cells comprise the protein-bound hemoglobin which gives it’s the colour. Hemoglobin functions include carrying oxygen from the lungs to all other parts of the body and carbon dioxide from the body to the lung which is exhaled. The red blood cells are mainly produced in the bone marrow and the body needs vital nutrients iron and vitamin B12 to secrete red blood cells\n" +
                    "\n" +
                    "Causes\n" +
                    "Anemia is caused due to several factors which include reduced production of hemoglobin due to iron deficiency, vitamin B12 deficiency, thalassemia disorder, and bone marrow disorder. Genetic conditions like sickle cell anemia, infections, and autoimmune disorder resulting in an increased breakdown of red blood cells. Anemia is also categorized based on the size of red blood cells, if the cells are small it is termed as microcytic anemia and if cells are large then it termed as macrocytic anemia. Anemia ranges from mild to severe form, however, anemia needs immediate medical attention as it may be a warning sign of other serious health conditions.\n" +
                    "\n" +
                    "Risk Factors\n" +
                    "Certain factors can increase the risk for anemia which includes a poor diet, intestinal problems such as celiac disease and Crohn’s disease, menstruation and menopause, pregnancy, chronic disease -cancer and kidney failure, genetic factors, alcoholism and exposure to toxic chemicals.\n" +
                    "\n" +
                    "The signs and symptoms of anemia differ depending upon the causes. Symptoms of anemia get worse as the condition becomes more severe.\n" +
                    "-Fatigue\n" +
                    "-Confusion\n" +
                    "-Lack of concentration\n" +
                    "-Weakness\n" +
                    "-Pale skin\n" +
                    "-Irregular heartbeat or Arrhythmia\n" +
                    "-Shortness of breath\n" +
                    "-Dizziness\n" +
                    "-Chest pain\n" +
                    "-Cold hands and feet\n" +
                    "\n" +
                    "\n" +
                    "Diagnosis And Treatment\n" +
                    "The doctor generally diagnoses anemia by doing a complete physical assessment and medical history. The doctor also suggest certain blood tests such as CBC to determine the underlying cause of anemia.\n" +
                    "\n" +
                    "Treatment usually depends upon treating the underlying causes which include - iron, vitamin B 12, folic acid and vitamin C supplements are provided to improve the hemoglobin level. If anemia is caused as a result of chronic diseases then blood transfusion is recommended to boosts iron levels, bone marrow transplantation and also treating other underlying conditions is vital.\n" +
                    "\n" +
                    "Disclaimer:\n" +
                    "The content provided here is for informational purposes only. This blog is not intended to substitute for medical advice, diagnosis, or treatment. Always seek the advice of a qualified healthcare provider for any questions or concerns you may have regarding a medical condition. Reliance does not endorse or recommend any specific tests, physicians, procedures, opinions, or other information mentioned on the blog.", R.drawable.img02),
            News("Foods Containing Iron Prevent Anemia", "Foods Containing Iron Prevent Anemia\n" +
                    "1. Black Beans\n" +
                    "Established as the king of iron-rich foods, black beans and kidney beans come out on top. Just a cup of cooked black beans or kidney beans can contribute 15.1 mg of iron, far exceeding the daily requirement for adults which is generally around 8 mg.\n" +
                    "\n" +
                    "2. Delicious Animal Source of Iron\n" +
                    "Beef and tuna lovers can rejoice! Beef and a piece of tuna filet each contain 2 mg and 2.5 mg of iron. Apart from being high in protein, these two food ingredients are also a source of iron which is easily absorbed by the body.\n" +
                    "\n" +
                    "3. Green vegetable\n" +
                    "For those who are vegetarian or are limiting their meat consumption, don't worry! Green vegetables such as spinach and broccoli are good plant-based sources of iron. Half a cup of boiled spinach contains 3.1 mg of iron, while a cup of steamed broccoli offers 1 mg of iron.\n" +
                    "\n" +
                    "To increase iron absorption from plant sources, consume these vegetables along with foods rich in vitamin C such as tomatoes.\n" +
                    "\n" +
                    "4.Tofu\n" +
                    "The versatile soy-based food manure turns out to be a surprising source of iron. Half a cup of tofu contains 3.4 mg of iron. Apart from that, tofu is also rich in high-quality vegetable protein.\n" +
                    "\n" +
                    "5. Shell\n" +
                    "Shellfish not only offer a unique taste, but also contain quite a bit of iron. Ten medium-sized shellfish that are cooked well contain about 2 mg of iron. Apart from shellfish, oysters and mussels are also known as good sources of iron.\n" +
                    "\n" +
                    "6. Pumpkin Seeds\n" +
                    "Pumpkin seeds, which we often ignore, actually contain quite high iron content. A cup of roasted pumpkin seeds offers 2.1 mg of iron. Apart from that, pumpkin seeds are also rich in healthy fats, protein and fiber.\n" +
                    "\n" +
                    "7. Chicken Breast and Beef Liver\n" +
                    "Chicken breast, even though its iron content is not as high as black beans, can still be a valuable source of iron in your menu. A cup of cooked skinless chicken breast contains about 1.3 mg of iron.\n" +
                    "\n" +
                    "For those who like offal, beef liver is the right choice. Beef liver is known as a food that is very rich in iron, but it needs to be consumed in moderation due to its high cholesterol content.", R.drawable.img03),
            News("What Is Anemia?","Anemia is a condition that develops when your blood produces a lower-than-normal amount of healthy red blood cells. If you have anemia, your body does not get enough oxygen-rich blood. The lack of oxygen can make you feel tired or weak. You may also have shortness of breath, dizziness, headaches, or an irregular heartbeat. According to the Centers for Disease Control and Preventionexternal link, about 3 million people in the United States have anemia.\n" +
                    "\n" +
                    "There are many types of anemia, including:\n" +
                    "-Iron-deficiency anemia\n" +
                    "-Vitamin B12-deficiency anemia\n" +
                    "-Hemolytic anemia\n" +
                    "\n" +
                    "Mild anemia is a common and treatable condition that can develop in anyone. It may come about suddenly or over time, and may be caused by your diet, medicines you take, or another medical condition. Anemia can also be chronic, meaning it lasts a long time and may never go away completely. Some types of anemia are inherited. The most common type of anemia is iron-deficiency anemia.\n" +
                    "\n" +
                    "Some people are at a higher risk for anemia, including women during their menstrual periods and pregnancy. People who do not get enough iron or certain vitamins and people who take certain medicines or treatments are also at a higher risk.\n" +
                    "\n" +
                    "Anemia may also be a sign of a more serious condition, such as bleeding in your stomach, inflammation from an infection, kidney disease, cancer, or autoimmune diseases. Your doctor will use your medical history, a physical exam, and test results to diagnose anemia.\n" +
                    "\n" +
                    "Treatments for anemia depend on the type you have and how serious it is. For some types of mild to moderate anemia, you may need iron supplements, vitamins, or medicines that make your body produce more red blood cells. To prevent anemia in the future, your doctor may also suggest healthy eating changes.", R.drawable.img04),
            News("Understanding Iron Deficiency Anemia","Iron deficiency anemia is a type of anemia that occurs due to the body lacking iron. This condition results in a decrease in the number of healthy red blood cells in the body.\n" +
                    "\n" +
                    "Iron is an important mineral that the body needs to produce one of the components of red blood cells, namely hemoglobin. Hemoglobin is a protein that functions to transport oxygen to be distributed throughout the body's tissues.\n" +
                    "\n" +
                    "When there is an iron deficiency, the body cannot produce enough hemoglobin. Lack of hemoglobin production reduces the oxygen supply in the blood so that the body does not get enough oxygen. This is what causes people with iron deficiency anemia to become easily tired, weak, and even short of breath.\n" +
                    "\n" +
                    "Iron deficiency anemia in pregnant women can interfere with the development of the fetus and baby. Apart from that, this condition also increases the risk of premature birth, infectious diseases, and maternal and child death.\n" +
                    "\n" +
                    "Iron deficiency anemia is the most common type of anemia, accounting for around 50% of all existing types of anemia.\n" +
                    "\n" +
                    "Causes and Symptoms of Iron Deficiency Anemia\n" +
                    "The causes of iron deficiency anemia vary. However, iron deficiency anemia generally occurs in people with the following conditions:\n" +
                    "-Consume less foods that contain iron\n" +
                    "- Experiencing absorption disorders in the gastrointestinal tract (malabsorption) so that they cannot absorb iron optimally\n" +
                    "- Experiencing bleeding\n" +
                    "- During pregnancy so more iron needed\n" +
                    "- Suffering from chronic kidney failure\n" +
                    "\n" +
                    "Symptoms of iron deficiency anemia can be difficult to detect if the degree is still mild. However, this condition can be characterized by pale skin, lethargy or weakness, fatigue, dizziness, decreased appetite, and a faster or pounding heartbeat.\n" +
                    "\n" +
                    "Treatment and Prevention of Iron Deficiency Anemia\n" +
                    "Iron deficiency anemia is generally easy to treat. The essence of treating iron deficiency anemia is to restore the body's iron supply and treat the underlying cause of iron deficiency.\n" +
                    "In order for iron levels in the body to return to normal, patients will be advised to increase iron intake, namely by consuming iron supplements and foods rich in iron.\n" +
                    "Meanwhile, treatment of the underlying causes of iron deficiency anemia depends on the type of disease. If the cause is heavy bleeding and the hemoglobin level is very low, the doctor can perform a red blood cell transfusion.\n" +
                    "Iron deficiency anemia can be prevented by consuming foods rich in iron, such as red meat, fish, legumes and wheat. Apart from that, consuming foods or drinks that contain vitamin C can also increase iron absorption.", R.drawable.img05),
            News("Why does anemia often occur in women?","Anemia. The name of this disease must have been familiar to you since you were at the end of elementary school or the beginning of middle school—when most of the female students in your class started menstruating.\n" +
                    "Unfortunately, this medical condition that is closely related to women is often overlooked. In fact, if left unchecked, anemia can continue from adolescence until you become pregnant and be passed on to your baby.\n" +
                    "WHO calls anemia a condition where hemoglobin (Hb) levels in the blood are lower than normal values. It is also defined as a condition when the number of red blood cells or oxygen carrying capacity is insufficient for the body's physiological needs.\n" +
                    "Adult women who are not pregnant can be categorized as suffering from anemia if the Hb concentration in their body is less than 12 g/dL, while in pregnant women it is less than 11 g/dL.\n" +
                    "Among pregnant women, anemia is often confused with blood pressure problems.\n" +
                    "\n" +
                    "“Often anemic pregnant women deny it by saying their blood pressure is normal. \"Even though those are two different things,\" said Prof. Dr. Endang Laksminingsih, MPH., Dr.PH, nutritionist and Coordinator of the Positive Deviance Resource Center (PDRC) under the auspices of the University of Indonesia.\n" +
                    "\n" +
                    "Hemoglobin is formed from a combination of protein and iron (Fe) in red blood cells. If you lack one of Fe, protein, folic acid, vitamin B12 or vitamin A, your Hb levels will also decrease. This condition is called anemia. Meanwhile, blood pressure refers to the pressure produced by the heart pump to move blood throughout the body. In one Hb molecule there are four Fe, each of which binds one oxygen. The oxygen in Hb is then circulated throughout the body to activate muscle and brain function. A person who is anemic will experience effects ranging from lethargy and weakness, to lowered intelligence levels.\n" +
                    "\n" +
                    "Based on 2019 WHO data, the prevalence of anemia in women of reproductive age 15-49 years around the world reached 29.9 percent or the equivalent of more than 500 million people—with almost half of them, 234 million people, found in the Southeast Asia region alone.\n" +
                    "In Indonesia, according to Basic Health Research or Riskesdas 2018, anemia attacks 38.5 percent of toddlers (babies under 5 years). The percentage in children aged 5-14 years is 26.8 percent and in the 15-24 year age category around 32 percent (meaning, anemia is found in 1 in 3 young people). This figure is even higher for pregnant women, namely 48.9 percent. Endang added, ideally you should start paying attention to your hemoglobin intake since adolescence, or after menstruation. The goal is that you still have sufficient Hb when you reach pregnancy. The critical period for the formation of fetal organs is in the first 8 weeks of pregnancy. Unfortunately, at that time many women were not aware of their pregnancy, so many suffered from anemia. In fact, it is during this period that the nutrients in the body are sucked up by the fetus to develop.\n" +
                    "If the mother is deficient in Hb, the fetus will adapt and develop without sufficient Hb. In the future, they will also suffer from anemia like their mother. The problem is, nutritional deficiencies in early pregnancy cannot be corrected in the following months. What does it mean? This means that you have to prepare your Hb intake long before you get pregnant.\n" +
                    "\n" +
                    "“Adolescent women who are not anemic during pregnancy are at risk of becoming anemic because their needs increase sharply. \"Especially those who have been anemic [since they were teenagers],\" said Endang.\n" +
                    "\n" +
                    "That is the reason behind the increasing prevalence of anemia sufferers from teenage girls to pregnant women. Apart from anemia which decreases in children, anemia in pregnant women can also cause babies to be born with low birth weight, premature birth and bleeding.\n" +
                    "\n" +
                    "Bad Habits Cause Anemia\n" +
                    "It turns out that there are also trivial habits that can increase the risk of developing anemia. This applies to those of you who like to drink green tea after eating with the aim of shedding body fat, including those who like to drink tea, coffee or milk after eating.\n" +
                    "Drinking tea, coffee and milk after eating can cause the body to lose Fe, which can lead to anemia. The tannin and phytate content in tea and coffee, as well as calcium and phosphorus in milk, can bind iron so that it is difficult to absorb. Are the types of drinks above prohibited for women? Not really. You can still enjoy tea, coffee or milk, as long as you leave a gap of about two hours after eating heavy food.\n" +
                    "Unfortunately, without a blood test, anemia is difficult to detect because it has symptoms similar to other nutritional deficiencies. The symptoms are quite common, such as pale skin, yellowish eyes, weak muscles, dizziness, fainting, fatigue, enlarged spleen or even heart attack.\n" +
                    "\n" +
                    "\"During menstruation, the red blood cells come out, automatically all the substances that are there, including Fe, protein, etc., also come out,\" explained Endang.\n" +
                    "\n" +
                    "Apart from menstruation and vitamin deficiencies, several other causes of anemia are infections. For example worms, malaria, tuberculosis, HIV/AIDS. Or hemolytic diseases such as thalassemia.\n" +
                    "\n" +
                    "So, what can you do to avoid anemia?\n" +
                    "The main thing is of course to consume balanced nutritious food, especially rich in iron, vitamins B and C including green vegetables such as spinach or kale, red beef and chicken, liver, fish and sea products, legumes, nuts and seeds and various fruits. such as oranges, papaya, mango, strawberries, beets.\n" +
                    "Consuming blood supplement tablets (TTD) is also highly recommended. Referring to the guidelines for administering TTD issued by the Ministry of Health in 2020, TTD can be given to teenage girls once a week or a total of 52 tablets for a whole year, while for pregnant women a minimum of a total of 90 tablets during the pregnancy process. It is recommended that tablets be consumed together with foods that contain vitamin C, for example orange juice, so that absorption is better.\n" +
                    "Don't worry, consuming TTD is absolutely not dangerous.\n" +
                    "\n" +
                    "Our body can basically regulate iron absorption. If the amount of iron is sufficient, the excess will be excreted in the feces.\n" +
                    "\n" +
                    "Don't forget, pay attention to the side effects of TTD such as nausea, vomiting, stomach pain, black stools, and constipation. Usually, these side effects are one of the factors that makes someone reluctant to consume it. That's right, isn't it?", R.drawable.img06),
            News("Find out the causes, symptoms and treatment of aplastic anemia","xDid you know that there are many types of anemia? Maybe you have often heard about anemia which is related to a lack of red blood cells which causes health problems, but it turns out there are more than 400 types of anemia, and one of them is aplastic anemia which you need to be aware of. What exactly is aplastic anemia? And how is aplastic anemia treated? Come find out more here!  \n" +
                    "\n" +
                    "What is aplastic anemia?  Aplastic anemia is a type of blood disorder that occurs due to a condition where the bone marrow has problems producing new blood cells in sufficient quantities. Aplastic anemia results in the bone marrow being unable to reproduce one or all types of red blood cells including white blood cells and platelets. \n" +
                    "\n" +
                    "Generally, this disease is experienced by those who have just reached the age of 20 and the elderly and this type of anemia is a rare and serious type of disease.\n" +
                    "\n" +
                    "Judging from the cause, aplastic anemia is divided into two, namely aplastic anemia that appears at a certain age (acquired aplastic anemia) and aplastic anemia that a person has had since birth (inherited aplastic anemia). Aplastic anemia due to genetic causes usually occurs due to gene damage in children.\n" +
                    "\n" +
                    "Factors causing aplastic anemia \n" +
                    "Several common causes of aplastic anemia, namely: \n" +
                    "1. Immune or autoimmune system disorders. \n" +
                    "Autoimmune occurs when the body's immune system attacks healthy cells, including cells in the bone marrow, causing the risk of developing aplastic anemia. When the immune system attacks the body itself, it also attacks stem cells (cells that do not yet have their own function so they can change and adapt depending on their location) which are in the bone marrow.\n" +
                    "\n" +
                    "2. Genetic or hereditary disease.\n" +
                    "If a family has a history of aplastic anemia, a child or their offspring also has a greater risk of developing aplastic anemia. \n" +
                    "\n" +
                    "3. Virus infection. \n" +
                    "Several viruses are closely related to aplastic anemia, including Hepatitis, Epstein-Barr, Cytomegalovirus, and human immunodeficiency virus (HIV). These viruses can attack the spinal cord. \n" +
                    "\n" +
                    "4. Radiotherapy and chemotherapy. \n" +
                    "Radiation therapy or radiotherapy and chemotherapy are also factors that cause aplastic anemia. This is because these two cancer treatment methods can damage stem cells.\n" +
                    "\n" +
                    "5. Use of certain medications.\n" +
                    "Use of drugs such as the antibiotic chloramphenicol (used to treat bacterial infections) and drugs for diseases that attack the joint system.\n" +
                    "\n" +
                    "Symptoms of aplastic anemia\n" +
                    "The symptoms that appear vary depending on the situation and condition of a person with aplastic anemia. However, generally, the symptoms that often occur include: \n" +
                    "\n" +
                    "-Fatigue \n" +
                    "-Pale skin \n" +
                    "-Headaches and dizziness \n" +
                    "-Fever \n" +
                    "-Skin bruises and bleeds more easily \n" +
                    "-Nosebleeds\n" +
                    "\n" +
                    "Aplastic anemia treatment and care\n" +
                    "If you experience symptoms of anemia, you can consult a doctor to get the right diagnosis and treatment. If you suffer from aplastic anemia, the treatment usually given by a doctor is: \n" +
                    "\n" +
                    "1. Antibiotics and antivirals\n" +
                    "People with anemia are more susceptible to infections, therefore, doctors will recommend antibiotics or antivirals.\n" +
                    "\n" +
                    "2. Blood transfusion. \n" +
                    "Blood transfusions are usually carried out if people with anemia experience a drastic and sudden lack of blood cells.\n" +
                    "\n" +
                    "3. Immunosuppressants or immunosuppressives.\n" +
                    "Administering drugs to suppress the immune system. This treatment is commonly used by patients with autoimmune disorders. \n" +
                    "\n" +
                    "4. Stem cell transplant. \n" +
                    "The stem cell transplant procedure is generally performed to replace damaged stem cells with healthy cells. This procedure is usually given when the patient has received immunosuppressant treatment but the patient's condition has not improved.", R.drawable.img07),
            News("Diet for Anemic Patient Recommended By the Nutritionist","Anemia is a health condition when your body has a deficiency of red blood cells. The main reason for this condition is blood loss, destruction of red blood cells or an individual body is not able to create the required amount of red blood cells. Diet plays an important role to manage anemia. Here we are exploring the food items\n" +
                    "\n" +
                    "Types of Anemia:\n" +
                    "-Iron-Deficiency Anemia: It is the most common type is anemia. RBCs contain a protein called hemoglobin. It contains a lot of iron. Due to iron deficiency, your body can’t able to produce sufficient hemoglobin it needs to create enough red blood cells to deliver oxygen-rich blood throughout your body. \n" +
                    "\n" +
                    "-Pernicious Anemia: A deficiency of vitamin B-12 and folate can also impact an individual’s ability to produce red blood cells. If an individual’s body is not able to process B-12 properly, you can have pernicious anemia. An iron-rich diet and B vitamins like the plan below is important if you have anemia.\n" +
                    "\n" +
                    "Tips for The Diet for Anemic Patient\n" +
                    "\n" +
                    "Foods Rich in Iron:\n" +
                    "-Nuts and seeds are part of a diet plan for anemia\n" +
                    "-Pumpkin seeds are a very good source of iron.\n" +
                    "-Many foods contain high levels of iron. That makes it easy for a person to combine them and make delicious.\n" +
                    "-nutritious meals that help to increase the intake of iron.\n" +
                    "\n" +
                    "Fruits and vegetables:\n" +
                    "-Watercress\n" +
                    "-Curly kale and other varieties\n" +
                    "-Spinach\n" +
                    "-Collard greens\n" +
                    "-Dandelion greens\n" +
                    "-Swiss chard\n" +
                    "-Citrus fruits\n" +
                    "-Red and yellow peppers\n" +
                    "-Broccoli\n" +
                    "\n" +
                    "However, some dark, leafy greens contain oxalates, which can resist the absorption of iron. So, then completely relying on only vegetables, a person should aim to get iron from different sources.\n" +
                    "\n" +
                    "Nuts and Seeds:\n" +
                    "-Pumpkin seeds\n" +
                    "-Cashews\n" +
                    "-Pistachios\n" +
                    "-Hemp seeds\n" +
                    "-Pine nuts\n" +
                    "-Sunflower seeds\n" +
                    "\n" +
                    "Meat and Fish:\n" +
                    "-Beef\n" +
                    "-Lamb\n" +
                    "-Liver\n" +
                    "-Shellfish\n" +
                    "-Shrimp\n" +
                    "-Sardines\n" +
                    "-Tuna\n" +
                    "-Salmon\n" +
                    "\n" +
                    "Dairy products:\n" +
                    "-Raw milk\n" +
                    "-Yogurt\n" +
                    "-Cheese\n" +
                    "\n" +
                    "Beans and Pulses:\n" +
                    "-Kidney beans\n" +
                    "-Chickpeas\n" +
                    "-Soybeans\n" +
                    "-Black-eyed peas\n" +
                    "-Pinto beans\n" +
                    "-Black beans\n" +
                    "-peas\n" +
                    "-Five beans\n" +
                    "\n" +
                    "Foods to Avoid:\n" +
                    "-Avoid drinking tea or coffee, it can affect the absorption of iron.\n" +
                    "-Outside food.\n" +
                    "-Spices.\n" +
                    "-Deep-fried.\n" +
                    "-Avoid alcohol.\n" +
                    "-Avoid calcium with Iron.\n" +
                    "\n" +
                    "Tips for Getting More Iron in the Diet\n" +
                    "\n" +
                    "Consuming iron-rich food in your daily routine is the best way to have iron. However, the following strategies can maximize a person’s iron intake:\n" +
                    "-Vitamin C enhances iron absorption, particularly from non-heme iron. Try to add citrus fruits, peppers, spinach, and other vitamin C-containing foods in your diet if you are having iron pills to enhance absorption. Immediately before and after taking iron pills, avoid taking tea and coffee, soy protein, high-fiber foods, and calcium supplements.\n" +
                    "\n" +
                    "-If you like to eat non-vegetarian food then you can add plenty of meat, especially organ meats. The heme iron found in meat is more readily absorbed than the non-heme form found in plants. lamb, Beef, dark-meat chicken, pork, oysters and liver are all sources of heme iron.\n" +
                    "\n" +
                    "-Be patient. It can sometimes take several months of increasing your iron intake before you see a noticeable increase in blood iron levels.", R.drawable.img08),
            News("A complete low down on anemia caused by iron deficiency","Anemia refers to decreased number of circulating red blood cells. It is the most common blood disorder in the general population and affects an estimated two billion people, globally. Iron deficiency is the most common cause of anemia, which is caused due to the lack of sufficient iron to form normal red blood cells. Iron is essential for the formation of hemoglobin, the main component of red blood cells for carrying oxygen from lungs to all the tissues in the body.\n" +
                    "\n" +
                    "Where does iron come from?\n" +
                    "Iron comes from the diet. Sources of food containing high levels of iron include red meat and liver. Smaller amounts are found in fortified breakfast cereals, beans, green leafy vegetables and eggs.\n" +
                    "\n" +
                    "What are the symptoms of iron deficiency anemia?\n" +
                    "Iron deficiency commonly causes tiredness, fatigue and pale skin. If severe, it can also cause breathlessness on exertion, hair loss, tinnitus (ringing in the ears) and occasionally strange cravings for non-food substances such as chalk and dirt (pica), especially in children.\n" +
                    "\n" +
                    "What are the causes of iron deficiency?\n" +
                    "In developing countries, low iron in the diet is the primary cause of iron deficiency anemia. In men and post-menopausal women, the most common cause is bleeding in the stomach and intestines. This can be caused by a stomach ulcer, stomach cancer, bowel cancer, or by taking painkillers. In women of reproductive age, excessive bleeding during periods and pregnancy are seen.\n" +
                    "\n" +
                    "How is iron deficiency diagnosed?\n" +
                    "The World Health Organisation defines anemia as blood hemoglobin values of less than 13 g/dl in men and 12g/dl for women. The diagnosis also includes a complete blood cell count, peripheral smear, reticulocyte count, and serum iron indices (serum ferritin and serum iron).\n" +
                    "\n" +
                    "How is iron deficiency treated?\n" +
                    "The treatment for iron deficiency anemia is oral iron supplementation. Oral supplementation is cheap, safe, and effective. The common side effects of oral treatment include gastritis, diarrhoea and constipation. Iron replacement is given through the vein if oral treatment is not tolerated due to side effects.\n" +
                    "\n" +
                    "Further problems…\n" +
                    "If iron deficiency anemia is left untreated, it can make us more susceptible to illness and infection, as lack of iron affects the body’s natural defence system (the immune system). Severe iron deficiency anemia may increase the risk of developing complications that affect the heart or lungs. Pregnant women with severe or untreated anemia also have a higher risk of complications before and after birth.", R.drawable.img09),
            News("Understanding the Impact of Anemia on Adolescents\n","Entering adolescence, of course there are many activities and hobbies that you want to pursue. Therefore, it is important for teenagers to continue to maintain their health and physical fitness, in order to avoid various types of health problems that can interfere with daily activities. One of the diseases to be wary of is anemia.\n" +
                    "\n" +
                    "Anemia is a disease that occurs when the body lacks healthy red blood cells or when red blood cells do not function properly. This causes the body's organs to not get enough oxygen, causing anemia sufferers to have pale skin and get tired easily.\n" +
                    "\n" +
                    "Anemia Concomitant Diseases\n" +
                    "Not only that, anemia also causes various health problems, including the following:\n" +
                    "\n" +
                    "1. Decreased immunity\n" +
                    "2. Decreased concentration\n" +
                    "3. Experiencing a decline in learning achievement\n" +
                    "4. Not fit and experiencing decreased productivity\n" +
                    "5. In teenage girls, anemia can increase the risk of death during childbirth, babies being born prematurely, and the baby's weight tends to be low.\n" +
                    " \n" +
                    "Tips to prevent Anemia\n" +
                    "It is hoped that the existence of various kinds of diseases caused by anemia in teenagers above will increase awareness among teenagers so they can start implementing various kinds of anemia prevention, such as consuming foods high in iron, folic acid, vitamins A, C, Zinc and consuming blood supplement tablets.\n" +
                    "\n" +
                    "Continue to adopt healthy lifestyle habits and immediately go to the nearest health facility for examinations if you experience symptoms of anemia, so you can get treatment as early as possible.", R.drawable.img10)
            // Add more news items here
        )
    }

    private fun getCalendarData(): ArrayList<CalendarData> {
        val calendarList = ArrayList<CalendarData>()
        val calendar = Calendar.getInstance()
        calendar.time = currentCalendar.time // Set calendar to current month and year
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Start at the beginning of the month

        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        while (calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
            calendarList.add(
                CalendarData(
                    date = calendar.time,
                    calendarDate = dateFormat.format(calendar.time),
                    calendarDay = dayFormat.format(calendar.time)
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendarList
    }

    private fun showMonthYearPicker() {
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, _ ->
            currentCalendar.set(Calendar.YEAR, selectedYear)
            currentCalendar.set(Calendar.MONTH, selectedMonth)
            updateMonthYearPickerText()
            updateCalendarData()
        }, year, month, currentCalendar.get(Calendar.DAY_OF_MONTH))

        // Hiding day field in DatePickerDialog
        try {
            val dayPickerId = resources.getIdentifier("android:id/day", null, null)
            if (dayPickerId != 0) {
                val dayPicker = datePickerDialog.datePicker.findViewById<View>(dayPickerId)
                dayPicker?.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        datePickerDialog.show()
    }

    private fun updateMonthYearPickerText() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding.monthYearPicker.text = dateFormat.format(currentCalendar.time)
    }

    private fun updateCalendarData() {
        calendarAdapter.updateList(getCalendarData())
    }

    override fun onItemClick(calendarData: CalendarData, position: Int) {
        // Show dialog to set reminder
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_medication_reminder, null)
        val editTextMedicationName = dialogView.findViewById<EditText>(R.id.editTextMedicationName)
        val editTextReminderTime = dialogView.findViewById<EditText>(R.id.editTextReminderTime)

        editTextReminderTime.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    editTextReminderTime.setText(timeFormat.format(calendar.time))
                },
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Set Medication Reminder")
            .setPositiveButton("Set") { dialog, _ ->
                val medicationName = editTextMedicationName.text.toString()
                val reminderTimeString = editTextReminderTime.text.toString()
                val reminderTime = Calendar.getInstance().apply {
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(reminderTimeString)!!
                    set(Calendar.YEAR, calendarData.date.year + 1900)  // Deprecated, consider using Calendar.YEAR
                    set(Calendar.MONTH, calendarData.date.month)  // Deprecated, consider using Calendar.MONTH
                    set(Calendar.DAY_OF_MONTH, calendarData.date.date)  // Deprecated, consider using Calendar.DAY_OF_MONTH
                }
                addEventToCalendar(reminderTime, medicationName)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addEventToCalendar(reminderTime: Calendar, medicationName: String) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, reminderTime.timeInMillis)
            put(CalendarContract.Events.DTEND, reminderTime.timeInMillis + 60 * 60 * 1000) // 1 hour event
            put(CalendarContract.Events.TITLE, medicationName)
            put(CalendarContract.Events.DESCRIPTION, "Medication Reminder")
            put(CalendarContract.Events.CALENDAR_ID, 1) // Assuming the user has at least one calendar, and it has an ID of 1
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

        val eventID = uri?.lastPathSegment?.toLong()
        if (eventID != null) {
            val reminderValues = ContentValues().apply {
                put(CalendarContract.Reminders.MINUTES, 10) // Reminder 10 minutes before
                put(CalendarContract.Reminders.EVENT_ID, eventID)
                put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            }
            contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
        }
    }

    private fun setupView() {
        // Additional setup when user is logged in
        supportActionBar?.hide()
        binding.bottomNavigation.selectedItemId = R.id.bottom_home
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> true
                R.id.bottom_scan -> {
                    navigateToOtherFeature(this, ScanActivity::class.java)
                    true
                }
                R.id.bottom_history -> {
                    navigateToOtherFeature(this, HistoryActivity::class.java)
                    true
                }
                R.id.bottom_account -> {
                    navigateToOtherFeature(this, AccountActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }
}
