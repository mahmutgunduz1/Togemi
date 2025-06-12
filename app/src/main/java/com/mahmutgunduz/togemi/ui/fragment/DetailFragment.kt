package com.mahmutgunduz.togemi.ui.fragment
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import android.provider.Settings
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.data.entity.NoteData
import com.mahmutgunduz.togemi.data.entity.FileData
import com.mahmutgunduz.togemi.databinding.FilepickerBottomSheetBinding
import com.mahmutgunduz.togemi.databinding.FragmentDetailBinding
import com.mahmutgunduz.togemi.databinding.NoteAddAlertDialogBinding
import com.mahmutgunduz.togemi.service.AlarmReceiver
import com.mahmutgunduz.togemi.ui.adapter.addFileAdapter
import com.mahmutgunduz.togemi.ui.viewmodel.DetailViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.DetailViewModelFactory
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModel
import com.mahmutgunduz.togemi.ui.viewmodel.MainViewModelFactory
import com.mahmutgunduz.togemi.service.alertDialogTrueFalse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteDataBase: com.mahmutgunduz.togemi.database.NoteDataBase
    private lateinit var noteData: NoteData


    private lateinit var noteDao: com.mahmutgunduz.togemi.database.NoteDao
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var adapter: addFileAdapter

    //değiştirilebilir bir liste (mutable list)
    private val fileList = mutableListOf<FileData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteDataBase = com.mahmutgunduz.togemi.database.NoteDataBase.getInstance(requireContext())
        noteDao = noteDataBase.noteDao()

        val detailFactory = DetailViewModelFactory(noteDao, noteDataBase.fileDao())
        detailViewModel = ViewModelProvider(this, detailFactory).get(DetailViewModel::class.java)

        // MainViewModel'i başlat
        val mainFactory = MainViewModelFactory(noteDao)
        mainViewModel =
            ViewModelProvider(requireActivity(), mainFactory).get(MainViewModel::class.java)
//-------------------------------
        val id = arguments?.getInt("id") ?: 0
        val title = arguments?.getString("title") ?: ""
        val noteText = arguments?.getString("noteText") ?: ""


        // noteData nesnesi oluşturuluyor
        noteData = NoteData(title = title, noteText = noteText).apply {
            this.id = id
        }

        detailViewModel.setNote(noteData)


        detailViewModel.note.observe(viewLifecycleOwner) { note ->
            binding.textTitle.text = note.title
            binding.textContent.text = note.noteText
            binding.textDate.text = getTimeAgo(note.date)
        }


        // Silme sonucunu gözle
        detailViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DetailViewModel.DeleteResult.Success -> {
                    Toast.makeText(requireContext(), "Not başarıyla silindi", Toast.LENGTH_SHORT)
                        .show()

                    val bundle = Bundle().apply {
                        putBoolean("noteDeleted", true)
                        putInt("deletedNoteId", noteData.id)
                    }

                    findNavController().navigate(R.id.action_detailFragment_to_mainFragment, bundle)
                }

                is DetailViewModel.DeleteResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }





        detailViewModel.getFilesForNote(noteData.id).observe(viewLifecycleOwner, Observer { files ->
            fileList.clear()
            fileList.addAll(files)
            adapter.notifyDataSetChanged()
        })
        //----


        setupClickListeners()

        //---
        readMode()
        backButton()
        saveFavoriteNote()
        moreOptions()
        updateNote()
        reminder()
        recyclerViewSetup()

    }


    private fun setupClickListeners() {
        binding.btnDelete.setOnClickListener {
            noteDelete()
        }
        binding.btnShare.setOnClickListener {
            shareNote(noteData)
        }
        binding.btnEdit.setOnClickListener {
            updateNote()
        }
        binding.btnAddAttachment.setOnClickListener {
            showFilePickerBottomSheet()
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnReminder.setOnClickListener {
            reminder()
        }
        binding.chipFavorite.setOnClickListener {
            saveFavoriteNote()
        }
        binding.chipReadMode.setOnCheckedChangeListener { _, isChecked ->
            readMode()
        }
        binding.btnMore.setOnClickListener {
            moreOptions()
        }
        binding.ivPinned.setOnClickListener {
            saveFavoriteNote()
        }


    }


    private fun noteDelete() {

        requireContext().alertDialogTrueFalse(
            title = "Notu Sil",
            message = "Notu silmek istediğinize emin misiniz?",
            positiveButtonText = "Evet",
            negativeButtonText = "Hayır",
            onPositiveClick = {
                detailViewModel.deleteNote(noteData)
            }
        )

    }

    private fun updateNote() {

        binding.btnEdit.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val dialogBinding = NoteAddAlertDialogBinding.inflate(layoutInflater)
            builder.setView(dialogBinding.root)

            dialogBinding.etSubject.setText(noteData.title)
            dialogBinding.etNote.setText(noteData.noteText)
            dialogBinding.tvTitle.text = "Notu Güncelle"
            val dialog = builder.create()
            dialog.show()

            dialogBinding.btnSave.setOnClickListener {

                val subject = dialogBinding.etSubject.text.toString()
                val note = dialogBinding.etNote.text.toString()

                if (subject.isNotEmpty() && note.isNotEmpty()) {
                    val updatedNote = NoteData(subject, note, noteData.date).apply {
                        this.id = noteData.id
                        this.date = System.currentTimeMillis()
                    }
                    detailViewModel.updateNote(updatedNote)
                    binding.textDate.text = getTimeAgo(updatedNote.date)
                    Toast.makeText(
                        requireContext(),
                        "Not başarıyla güncellendi",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.textTitle.text = updatedNote.title
                    binding.textContent.text = updatedNote.noteText


                    dialog.dismiss()
                } else {

                    Toast.makeText(requireContext(), "Alanlar boş olamaz", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        binding.btnAddAttachment.setOnClickListener {
            showFilePickerBottomSheet()
        }
    }

    private fun recyclerViewSetup() {
        adapter = addFileAdapter(fileList, ::onFileDeleteClick)
        binding.recyclerAttachments.adapter = adapter
        binding.recyclerAttachments.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                handleSelectedFile(it)
            }
        }


    private fun handleSelectedFile(uri: Uri) {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            it.moveToFirst()
            val name = it.getString(nameIndex)
            val size = it.getLong(sizeIndex)
            val type = requireContext().contentResolver.getType(uri) ?: "Bilinmiyor"

            val newFile = FileData(
                title = name,
                size = size,
                type = type,
                image = uri.toString(),
                uri = uri,
                noteId = noteData.id

            )

            fileList.add(newFile)
            adapter.notifyItemInserted(fileList.size - 1)

            // Yeni oluşturulan dosyayı veritabanına ekle
            detailViewModel.fileInsert(newFile)



            Toast.makeText(requireContext(), "Dosya eklendi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showFilePickerBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val binding = FilepickerBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.ivImage.setOnClickListener {
            pickFileLauncher.launch("image/*")
            dialog.dismiss()
        }

        binding.ivVideo.setOnClickListener {
            pickFileLauncher.launch("video/*")
            dialog.dismiss()
        }

        binding.ivAudio.setOnClickListener {
            pickFileLauncher.launch("audio/*")
            dialog.dismiss()
        }

        binding.ivDocument.setOnClickListener {
            pickFileLauncher.launch("*/*")
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun onFileDeleteClick(file: FileData) {
        requireContext().alertDialogTrueFalse(
            title = "Dosyayı  Sil",
            message = "Dosyayı  silmek istediğinize emin misiniz?",
            positiveButtonText = "Evet",
            negativeButtonText = "Hayır",
            onPositiveClick = {

                detailViewModel.fileDelete(file)
                fileList.remove(file)
                adapter.notifyDataSetChanged()

                Toast.makeText(requireContext(), "Dosya silindi", Toast.LENGTH_SHORT).show()

            }
        )


    }


    private fun backButton() {

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun readMode() {
        binding.chipReadMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.textTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )

                binding.chipGroup.visibility = View.GONE
                binding.tvAttachmentsTitle.visibility = View.GONE
                binding.recyclerAttachments.visibility = View.GONE
                binding.tvEmptyAttachments.visibility = View.GONE
                binding.cardAttachments.visibility = View.GONE
                binding.tvModesTitle.visibility = View.GONE
                binding.chipNightMode.visibility = View.GONE
                binding.chipFavorite.visibility = View.GONE
                binding.tvInfo.visibility = View.GONE
                binding.ivPinned.visibility = View.GONE
                binding.btnReminder.visibility = View.GONE

                binding.textDate.visibility = View.GONE
                binding.textLastEdited.visibility = View.GONE

                binding.bottomAppBar.visibility = View.GONE

            } else {
                binding.chipGroup.visibility = View.VISIBLE
                binding.tvAttachmentsTitle.visibility = View.VISIBLE
                binding.recyclerAttachments.visibility = View.VISIBLE
                binding.tvEmptyAttachments.visibility = View.VISIBLE
                binding.cardAttachments.visibility = View.VISIBLE
                binding.tvModesTitle.visibility = View.VISIBLE
                binding.chipNightMode.visibility = View.VISIBLE
                binding.chipFavorite.visibility = View.VISIBLE
                binding.tvInfo.visibility = View.VISIBLE
                binding.ivPinned.visibility = View.VISIBLE
                binding.btnReminder.visibility = View.VISIBLE
                binding.bottomAppBar.visibility = View.VISIBLE

            }
        }
    }


    private fun shareNote(note: NoteData) {
        val shareIntent = Intent().apply {
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT, "Başlık: ${note.title}\n\nİçerik: ${note.noteText} " +
                        "\n\nTarih: ${sdf.format(note.date)}  "
            )
            type = "text/plain"
        }
        // Uygulama seçici çıkart (kullanıcı hangi uygulamayla paylaşacaksa onu seçebilir)
        val chooser = Intent.createChooser(shareIntent, "Notu paylaş")
        if (shareIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(requireContext(), "Paylaşacak uygulama bulunamadı", Toast.LENGTH_SHORT)
                .show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun getTimeAgo(timeInMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timeInMillis

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "$seconds saniye önce"
            minutes < 60 -> "$minutes dakika önce"
            hours < 24 -> "$hours saat önce"
            days < 7 -> "$days gün önce"
            else -> {
                val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("tr"))
                dateFormat.format(java.util.Date(timeInMillis))
            }
        }
    }

    fun reminder() {

        binding.btnReminder.setOnClickListener {
            val calendar = Calendar.getInstance()

            context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    { _, year, month, dayOfMonth ->
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                calendar.set(year, month, dayOfMonth, hourOfDay, minute, 0)
                                setAlarm(calendar.timeInMillis) // Alarmı kur
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }


        }
    }

    private fun setAlarm(timeInMillis: Long) {
        val ctx = context ?: run {
            Toast.makeText(context, "Context bulunamadı", Toast.LENGTH_SHORT).show()
            return
        }

        if (timeInMillis <= System.currentTimeMillis()) {
            Toast.makeText(ctx, "Geçersiz alarm zamanı", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:" + ctx.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // Çok önemli!
                }
                ctx.startActivity(intent)
                return
            }
        }

        val intent = Intent(ctx, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)

        Toast.makeText(ctx, "Alarm kuruldu!", Toast.LENGTH_SHORT).show()
    }

    private fun moreOptions() {

        binding.btnMore.setOnClickListener {

            val popupMenu = androidx.appcompat.widget.PopupMenu(requireContext(), it)
            popupMenu.inflate(R.menu.details_options_menu)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (menuItem.itemId) {
                    R.id.detailFavorites -> {
                        Toast.makeText(requireContext(), "Favoriler", Toast.LENGTH_SHORT).show()

                        findNavController().navigate(R.id.action_detailFragment_to_favoriteNotesFragment)

                        true
                    }
                }
                true
            }
        }
    }


    private fun saveFavoriteNote() {
        // İlk başta mevcut favori durumuna göre ikonu ayarla
        updateFavoriteIcon(noteData.isFavorite)

        binding.ivPinned.setOnClickListener {
            // Favori durumunu tersine çevir
            noteData.isFavorite = !noteData.isFavorite

            // Veritabanını güncelle
            detailViewModel.updateNote(noteData)

            // İkonu güncelle
            updateFavoriteIcon(noteData.isFavorite)

            // Kullanıcıya geri bildirim ver
            val message = if (noteData.isFavorite) {


                "Not favorilere eklendi"
            } else {
                "Not favorilerden çıkarıldı"
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            // Favori ise altın renkli dolu kalp
            binding.ivPinned.setImageResource(R.drawable.ic_favorite_filled_gold)
            // Animasyon efekti
            binding.ivPinned.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .withEndAction {
                    binding.ivPinned.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        } else {
            // Favori değilse boş kalp
            binding.ivPinned.setImageResource(R.drawable.ic_favorite_outline)
        }

        // İkonu tam görünür yap
        binding.ivPinned.alpha = 1.0f
    }

}
